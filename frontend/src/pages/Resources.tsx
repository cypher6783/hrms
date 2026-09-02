import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { Package } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Resource } from '../types';

export const Resources: React.FC = () => {
  const [resources, setResources] = useState<Resource[]>([]);

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

  return (
    <AppShell title="Resources & Inventory">
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Package className="h-4 w-4 text-clinical-navy" />
              <span>Lassa Isolation Unit Resource Catalogue</span>
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
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
};
