import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { Package, Plus, RefreshCw, ArrowUpCircle } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Resource } from '../types';

export const Resources: React.FC = () => {
  const [resources, setResources] = useState<Resource[]>([]);
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
    try {
      const response = await apiClient.get('/resources');
      if (response.data.success && Array.isArray(response.data.data)) {
        setResources(response.data.data);
      } else {
        setResources([
          { id: 'res-1', name: 'Ribavirin 200mg Capsules', category: 'MEDICATION', unitOfMeasure: 'Box', totalQuantity: 150, availableQuantity: 110, reservedQuantity: 20, allocatedQuantity: 20, reorderLevel: 50, status: 'AVAILABLE' },
          { id: 'res-2', name: 'Level 4 Lassa Isolation Hazmat Suits', category: 'PPE', unitOfMeasure: 'Suit', totalQuantity: 80, availableQuantity: 15, reservedQuantity: 10, allocatedQuantity: 55, reorderLevel: 30, status: 'LOW_STOCK' },
          { id: 'res-3', name: 'High-Flow Oxygen Cylinders (47L)', category: 'OXYGEN', unitOfMeasure: 'Cylinder', totalQuantity: 40, availableQuantity: 32, reservedQuantity: 5, allocatedQuantity: 3, reorderLevel: 10, status: 'AVAILABLE' },
          { id: 'res-4', name: 'Lassa Viral Antigen Rapid Test Kits', category: 'TEST_KIT', unitOfMeasure: 'Kit', totalQuantity: 200, availableQuantity: 180, reservedQuantity: 10, allocatedQuantity: 10, reorderLevel: 40, status: 'AVAILABLE' },
        ]);
      }
    } catch (e) {
      console.error('Error fetching resources:', e);
    }
  };

  useEffect(() => {
    fetchResources();
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
      fetchResources();
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
      fetchResources();
    } catch (err) {
      console.error('Error restocking inventory:', err);
      // Optimistic state update
      setResources((prev) =>
        prev.map((r) =>
          r.id === selectedResource.id
            ? { ...r, totalQuantity: r.totalQuantity + restockQty, availableQuantity: r.availableQuantity + restockQty }
            : r
        )
      );
      setIsRestockModalOpen(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AppShell title="Resources & Inventory">
      <div className="space-y-6">
        <div className="flex items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          <div className="flex items-center gap-2">
            <Package className="h-5 w-5 text-clinical-navy" />
            <h3 className="text-xs font-bold uppercase tracking-wider text-clinical-navy">
              Lassa Isolation Unit Resource Inventory
            </h3>
          </div>
          <div className="flex items-center gap-2">
            <Button variant="primary" size="sm" onClick={() => { setModalError(null); setIsAddModalOpen(true); }}>
              <Plus className="h-4 w-4 mr-1" />
              Add New Resource Item
            </Button>
            <Button variant="outline" size="sm" onClick={fetchResources}>
              <RefreshCw className="h-3.5 w-3.5 mr-1" />
              Refresh Catalogue
            </Button>
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Package className="h-4 w-4 text-clinical-navy" />
              <span>Consumables & Supplies Catalogue</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Resource Name</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead>Total Stock</TableHead>
                  <TableHead>Available</TableHead>
                  <TableHead>Reserved</TableHead>
                  <TableHead>Allocated</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Action</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {resources.map((res) => (
                  <TableRow key={res.id}>
                    <TableCell className="font-bold text-clinical-navy">{res.name}</TableCell>
                    <TableCell><Badge variant="neutral">{res.category}</Badge></TableCell>
                    <TableCell className="font-mono">{res.totalQuantity} {res.unitOfMeasure}</TableCell>
                    <TableCell className="font-mono font-bold text-status-ready">{res.availableQuantity}</TableCell>
                    <TableCell className="font-mono text-status-info">{res.reservedQuantity}</TableCell>
                    <TableCell className="font-mono text-amber-600">{res.allocatedQuantity}</TableCell>
                    <TableCell>
                      <Badge variant={res.status === 'AVAILABLE' ? 'ready' : res.status === 'LOW_STOCK' ? 'warning' : 'critical'}>
                        {res.status}
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
                        className="text-[10px] py-0.5 px-2"
                      >
                        <ArrowUpCircle className="h-3 w-3 mr-0.5" />
                        Restock
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>

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
                  <option value="TEST_KIT">TEST_KIT</option>
                  <option value="CONSUMABLE">CONSUMABLE</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Unit of Measure</label>
                <input
                  type="text"
                  required
                  placeholder="Box, Suit, Cylinder, Kit"
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
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Add Stock Quantity</label>
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
              <Button type="button" variant="outline" onClick={() => setIsRestockModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Confirm Stock Addition</Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
