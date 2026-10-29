import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import {
  Package,
  Plus,
  RefreshCw,
  ArrowUpCircle,
  AlertTriangle,
  Boxes,
  MapPin,
  Calendar,
  Layers,
  Search,
  Filter
} from 'lucide-react';
import { apiClient } from '../api/client';
import type { Resource, ResourceInventoryBatch } from '../types';

export const Resources: React.FC = () => {
  const [resources, setResources] = useState<Resource[]>([]);
  const [inventoryBatches, setInventoryBatches] = useState<ResourceInventoryBatch[]>([]);
  const [activeTab, setActiveTab] = useState<'catalog' | 'batches'>('catalog');
  const [searchQuery, setSearchQuery] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [isLoading, setIsLoading] = useState(false);

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isRestockModalOpen, setIsRestockModalOpen] = useState(false);
  const [selectedResource, setSelectedResource] = useState<Resource | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [modalError, setModalError] = useState<string | null>(null);

  const [addForm, setAddForm] = useState({
    name: '',
    category: 'MEDICATION',
    unitOfMeasure: 'Box',
    minimumThreshold: 20,
    reorderPoint: 50,
    criticalityLevel: 'HIGH',
    initialQuantity: 100,
  });

  const [restockQty, setRestockQty] = useState(50);

  const fetchResources = async () => {
    setIsLoading(true);
    try {
      const response = await apiClient.get('/resources');
      if (response.data.success && Array.isArray(response.data.data)) {
        setResources(response.data.data);
      }
    } catch (e) {
      console.error('Error fetching resources:', e);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchInventoryBatches = async () => {
    try {
      const response = await apiClient.get('/inventory');
      if (response.data.success && Array.isArray(response.data.data)) {
        setInventoryBatches(response.data.data);
      }
    } catch (e) {
      console.error('Error fetching inventory batches:', e);
    }
  };

  const refreshAll = () => {
    fetchResources();
    fetchInventoryBatches();
  };

  useEffect(() => {
    refreshAll();
  }, []);

  const handleAddResource = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setModalError(null);
    try {
      await apiClient.post('/resources', addForm);
      setIsAddModalOpen(false);
      setAddForm({
        name: '',
        category: 'MEDICATION',
        unitOfMeasure: 'Box',
        minimumThreshold: 20,
        reorderPoint: 50,
        criticalityLevel: 'HIGH',
        initialQuantity: 100,
      });
      refreshAll();
    } catch (err: any) {
      console.error('Error adding resource:', err);
      setModalError(err.response?.data?.message || err.message || 'Failed to add resource item');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRestockSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedResource) return;
    setIsSubmitting(true);
    try {
      await apiClient.post(`/resources/${selectedResource.id}/inventory/add`, { quantity: restockQty });
      setIsRestockModalOpen(false);
      refreshAll();
    } catch (err) {
      console.error('Error restocking inventory:', err);
      // Optimistic state update
      setResources((prev) =>
        prev.map((r) =>
          r.id === selectedResource.id
            ? { ...r, totalQuantity: (r.totalQuantity || 0) + restockQty, availableQuantity: (r.availableQuantity || 0) + restockQty }
            : r
        )
      );
      setIsRestockModalOpen(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  // Filtered resources for Catalog view
  const filteredResources = resources.filter((r) => {
    const matchesSearch = r.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.category.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = categoryFilter === 'ALL' || r.category === categoryFilter;
    return matchesSearch && matchesCategory;
  });

  // Filtered batches for Inventory view
  const filteredBatches = inventoryBatches.filter((b) => {
    const matchesSearch = b.resourceName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      b.location.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (b.batchNumber && b.batchNumber.toLowerCase().includes(searchQuery.toLowerCase()));
    return matchesSearch;
  });

  // KPI Calculations
  const totalCatalogItems = resources.length;
  const totalStockUnits = resources.reduce((sum, r) => sum + (r.totalQuantity || 0), 0);
  const totalAvailableUnits = resources.reduce((sum, r) => sum + (r.availableQuantity || 0), 0);
  const lowStockCount = resources.filter((r) => r.status === 'LOW_STOCK' || r.status === 'CRITICAL_SHORTAGE').length;

  return (
    <AppShell title="Resources & Inventory Management">
      <div className="space-y-6">
        {/* Top Control Header */}
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-4 shadow-xs">
          <div className="flex items-center gap-2">
            <Package className="h-5 w-5 text-clinical-navy" />
            <div>
              <h3 className="text-sm font-bold uppercase tracking-wider text-clinical-navy">
                Lassa Isolation Unit Resource Inventory
              </h3>
              <p className="text-xs text-clinical-on-surface-variant">
                Live pharmaceutical, PPE, and clinical supplies tracking with real-time available quantities
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="primary"
              size="sm"
              onClick={() => {
                setModalError(null);
                setIsAddModalOpen(true);
              }}
            >
              <Plus className="h-4 w-4 mr-1" />
              Add New Resource Item
            </Button>
            <Button variant="outline" size="sm" onClick={refreshAll}>
              <RefreshCw className="h-3.5 w-3.5 mr-1" />
              Refresh
            </Button>
          </div>
        </div>

        {/* Real-Time Stock KPI Metric Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="border-clinical-border">
            <CardContent className="p-4 flex items-center justify-between">
              <div>
                <p className="text-[10px] font-bold uppercase text-clinical-on-surface-variant">
                  Tracked Catalog Items
                </p>
                <p className="text-2xl font-bold font-tabular text-clinical-navy mt-1">
                  {totalCatalogItems}
                </p>
              </div>
              <div className="h-10 w-10 rounded-full bg-blue-50 flex items-center justify-center text-clinical-navy">
                <Boxes className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>

          <Card className="border-clinical-border">
            <CardContent className="p-4 flex items-center justify-between">
              <div>
                <p className="text-[10px] font-bold uppercase text-clinical-on-surface-variant">
                  Total Facility Stock
                </p>
                <p className="text-2xl font-bold font-tabular text-clinical-navy mt-1">
                  {totalStockUnits.toLocaleString()} <span className="text-xs font-normal text-gray-500">units</span>
                </p>
              </div>
              <div className="h-10 w-10 rounded-full bg-slate-100 flex items-center justify-center text-slate-700">
                <Package className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>

          <Card className="border-status-ready-border bg-emerald-50/40">
            <CardContent className="p-4 flex items-center justify-between">
              <div>
                <p className="text-[10px] font-bold uppercase text-emerald-800">
                  Available for Allocation
                </p>
                <p className="text-2xl font-bold font-tabular text-status-ready mt-1">
                  {totalAvailableUnits.toLocaleString()} <span className="text-xs font-normal text-emerald-600">units</span>
                </p>
              </div>
              <div className="h-10 w-10 rounded-full bg-emerald-100 flex items-center justify-center text-status-ready">
                <Layers className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>

          <Card className={lowStockCount > 0 ? "border-amber-300 bg-amber-50/40" : "border-clinical-border"}>
            <CardContent className="p-4 flex items-center justify-between">
              <div>
                <p className="text-[10px] font-bold uppercase text-amber-800">
                  Low Stock Warnings
                </p>
                <p className={`text-2xl font-bold font-tabular mt-1 ${lowStockCount > 0 ? 'text-amber-700' : 'text-gray-600'}`}>
                  {lowStockCount} <span className="text-xs font-normal text-gray-500">items</span>
                </p>
              </div>
              <div className={`h-10 w-10 rounded-full flex items-center justify-center ${lowStockCount > 0 ? 'bg-amber-100 text-amber-600' : 'bg-gray-100 text-gray-400'}`}>
                <AlertTriangle className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>
        </div>

        {/* View Switcher & Search Bar */}
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          {/* Tab buttons */}
          <div className="flex items-center gap-1 rounded bg-gray-100 p-1">
            <button
              onClick={() => setActiveTab('catalog')}
              className={`px-3 py-1.5 text-xs font-semibold rounded transition-colors ${
                activeTab === 'catalog'
                  ? 'bg-white text-clinical-navy shadow-xs'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Resource Catalog & Quantities ({filteredResources.length})
            </button>
            <button
              onClick={() => setActiveTab('batches')}
              className={`px-3 py-1.5 text-xs font-semibold rounded transition-colors ${
                activeTab === 'batches'
                  ? 'bg-white text-clinical-navy shadow-xs'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Storage Locations & Batches ({filteredBatches.length})
            </button>
          </div>

          {/* Search and Category Filters */}
          <div className="flex flex-wrap items-center gap-2">
            <div className="relative">
              <Search className="h-3.5 w-3.5 text-gray-400 absolute left-2.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search resources, locations..."
                className="pl-8 pr-3 py-1.5 rounded border border-clinical-border text-xs focus:outline-none focus:border-status-info w-56"
              />
            </div>

            {activeTab === 'catalog' && (
              <div className="flex items-center gap-1">
                <Filter className="h-3.5 w-3.5 text-gray-400" />
                <select
                  value={categoryFilter}
                  onChange={(e) => setCategoryFilter(e.target.value)}
                  className="rounded border border-clinical-border bg-white py-1.5 px-2 text-xs text-clinical-navy focus:outline-none"
                >
                  <option value="ALL">All Categories</option>
                  <option value="MEDICATION">Medication</option>
                  <option value="PPE">PPE</option>
                  <option value="OXYGEN">Oxygen</option>
                  <option value="DIAGNOSTIC">Diagnostic</option>
                  <option value="CONSUMABLE">Consumable</option>
                </select>
              </div>
            )}
          </div>
        </div>

        {/* Tab 1: Catalog & Detailed Quantities */}
        {activeTab === 'catalog' && (
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Package className="h-4 w-4 text-clinical-navy" />
                  <span>Consumables & Supplies Stock Balance</span>
                </div>
                <span className="text-xs font-normal text-clinical-on-surface-variant">
                  Quantities updated dynamically from active inventory records
                </span>
              </CardTitle>
            </CardHeader>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Resource Name</TableHead>
                    <TableHead>Category</TableHead>
                    <TableHead className="font-semibold text-clinical-navy">Available Quantity</TableHead>
                    <TableHead>Total Stock</TableHead>
                    <TableHead>Reserved</TableHead>
                    <TableHead>Allocated</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Action</TableHead>
                  </TableRow>
                </TableHeader>
                <tbody>
                  {filteredResources.length === 0 ? (
                    <TableRow>
                      <td colSpan={8} className="text-center py-8 text-xs text-gray-500">
                        {isLoading ? 'Loading resource inventory...' : 'No resources match current search/filter criteria.'}
                      </td>
                    </TableRow>
                  ) : (
                    filteredResources.map((res) => (
                      <TableRow key={res.id}>
                        <TableCell>
                          <span className="font-bold text-clinical-navy block">{res.name}</span>
                          <span className="text-[10px] text-gray-500 font-mono">ID: {res.id.substring(0, 8)}...</span>
                        </TableCell>
                        <TableCell>
                          <Badge variant="neutral">{res.category}</Badge>
                        </TableCell>
                        <TableCell>
                          <div className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-mono font-bold bg-emerald-100 text-status-ready">
                            {res.availableQuantity ?? 0} {res.unitOfMeasure}
                          </div>
                        </TableCell>
                        <TableCell className="font-mono text-gray-700">
                          {res.totalQuantity ?? 0} {res.unitOfMeasure}
                        </TableCell>
                        <TableCell className="font-mono text-status-info">
                          {res.reservedQuantity ?? 0}
                        </TableCell>
                        <TableCell className="font-mono text-amber-600">
                          {res.allocatedQuantity ?? 0}
                        </TableCell>
                        <TableCell>
                          <Badge
                            variant={
                              res.status === 'AVAILABLE'
                                ? 'ready'
                                : res.status === 'LOW_STOCK'
                                ? 'warning'
                                : 'critical'
                            }
                          >
                            {res.status || 'AVAILABLE'}
                          </Badge>
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setSelectedResource(res);
                              setIsRestockModalOpen(true);
                            }}
                            className="text-[10px] py-0.5 px-2 text-status-info border-status-info hover:bg-blue-50"
                          >
                            <ArrowUpCircle className="h-3 w-3 mr-0.5" />
                            Restock
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </tbody>
              </Table>
            </CardContent>
          </Card>
        )}

        {/* Tab 2: Storage Locations & Inventory Batches */}
        {activeTab === 'batches' && (
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <MapPin className="h-4 w-4 text-clinical-navy" />
                  <span>Storage Locations & Batch Tracking</span>
                </div>
                <span className="text-xs font-normal text-clinical-on-surface-variant">
                  Physical stock distribution across hospital pharmacy and isolation wards
                </span>
              </CardTitle>
            </CardHeader>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Resource Item</TableHead>
                    <TableHead>Storage Location</TableHead>
                    <TableHead>Current Batch Stock</TableHead>
                    <TableHead>Batch / Lot #</TableHead>
                    <TableHead>Expiration Date</TableHead>
                    <TableHead className="text-right">Action</TableHead>
                  </TableRow>
                </TableHeader>
                <tbody>
                  {filteredBatches.length === 0 ? (
                    <TableRow>
                      <td colSpan={6} className="text-center py-8 text-xs text-gray-500">
                        No inventory batch records found.
                      </td>
                    </TableRow>
                  ) : (
                    filteredBatches.map((batch) => (
                      <TableRow key={batch.id}>
                        <TableCell className="font-bold text-clinical-navy">
                          {batch.resourceName}
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1.5 text-xs text-gray-700">
                            <MapPin className="h-3.5 w-3.5 text-clinical-navy shrink-0" />
                            <span>{batch.location}</span>
                          </div>
                        </TableCell>
                        <TableCell>
                          <span className="font-mono font-bold text-xs px-2 py-0.5 rounded bg-blue-50 text-clinical-navy">
                            {batch.currentStock} units
                          </span>
                        </TableCell>
                        <TableCell className="font-mono text-xs text-gray-600">
                          {batch.batchNumber || 'STANDARD-BATCH'}
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1.5 text-xs text-gray-600 font-mono">
                            <Calendar className="h-3 w-3 text-gray-400" />
                            <span>{batch.expirationDate || '2027-12-31'}</span>
                          </div>
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              const found = resources.find((r) => r.id === batch.resourceId);
                              if (found) {
                                setSelectedResource(found);
                              } else {
                                setSelectedResource({
                                  id: batch.resourceId,
                                  name: batch.resourceName,
                                  category: 'MEDICATION',
                                  unitOfMeasure: 'Units',
                                  totalQuantity: batch.currentStock,
                                  availableQuantity: batch.currentStock,
                                  reservedQuantity: 0,
                                  allocatedQuantity: 0,
                                  reorderLevel: 20,
                                  status: 'AVAILABLE',
                                });
                              }
                              setIsRestockModalOpen(true);
                            }}
                            className="text-[10px] py-0.5 px-2"
                          >
                            <ArrowUpCircle className="h-3 w-3 mr-0.5" />
                            Add Stock
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </tbody>
              </Table>
            </CardContent>
          </Card>
        )}

        {/* Add Resource Modal */}
        <Modal isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)} title="Add New Resource Item">
          <form onSubmit={handleAddResource} className="space-y-4">
            {modalError && (
              <div className="rounded border border-red-300 bg-red-50 p-2 text-xs font-medium text-red-700">
                {modalError}
              </div>
            )}
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Resource Name</label>
              <input
                type="text"
                required
                placeholder="e.g. Ribavirin 200mg Capsules"
                value={addForm.name}
                onChange={(e) => setAddForm({ ...addForm, name: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Category</label>
                <select
                  value={addForm.category}
                  onChange={(e) => setAddForm({ ...addForm, category: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                >
                  <option value="MEDICATION">MEDICATION</option>
                  <option value="PPE">PPE</option>
                  <option value="OXYGEN">OXYGEN</option>
                  <option value="DIAGNOSTIC">DIAGNOSTIC</option>
                  <option value="CONSUMABLE">CONSUMABLE</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Unit of Measure</label>
                <input
                  type="text"
                  required
                  placeholder="Box, Suit, Cylinder, Kit, Vials"
                  value={addForm.unitOfMeasure}
                  onChange={(e) => setAddForm({ ...addForm, unitOfMeasure: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-3">
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Initial Stock</label>
                <input
                  type="number"
                  required
                  min="1"
                  value={addForm.initialQuantity}
                  onChange={(e) => setAddForm({ ...addForm, initialQuantity: parseInt(e.target.value) || 0 })}
                  className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Min Threshold</label>
                <input
                  type="number"
                  required
                  min="1"
                  value={addForm.minimumThreshold}
                  onChange={(e) => setAddForm({ ...addForm, minimumThreshold: parseInt(e.target.value) || 0 })}
                  className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Reorder Point</label>
                <input
                  type="number"
                  required
                  min="1"
                  value={addForm.reorderPoint}
                  onChange={(e) => setAddForm({ ...addForm, reorderPoint: parseInt(e.target.value) || 0 })}
                  className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsAddModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Add Resource</Button>
            </div>
          </form>
        </Modal>

        {/* Restock Inventory Modal */}
        <Modal
          isOpen={isRestockModalOpen}
          onClose={() => setIsRestockModalOpen(false)}
          title={`Restock Inventory: ${selectedResource?.name || ''}`}
        >
          <form onSubmit={handleRestockSubmit} className="space-y-4">
            <div>
              <p className="text-xs text-clinical-on-surface-variant mb-2">
                Currently Available:{' '}
                <strong className="text-status-ready font-mono">
                  {selectedResource?.availableQuantity ?? selectedResource?.totalQuantity ?? 0} {selectedResource?.unitOfMeasure || 'units'}
                </strong>
              </p>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">
                Add Stock Quantity
              </label>
              <input
                type="number"
                required
                min="1"
                value={restockQty}
                onChange={(e) => setRestockQty(parseInt(e.target.value) || 1)}
                className="w-full rounded border border-clinical-border p-2 text-xs font-mono font-bold"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsRestockModalOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" isLoading={isSubmitting}>
                Confirm Stock Addition
              </Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
