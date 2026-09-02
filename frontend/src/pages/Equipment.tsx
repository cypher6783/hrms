import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { Stethoscope } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Equipment } from '../types';

export const EquipmentPage: React.FC = () => {
  const [equipmentList, setEquipmentList] = useState<Equipment[]>([]);

  useEffect(() => {
    const fetchEquipment = async () => {
      try {
        const response = await apiClient.get('/equipment');
        if (response.data.success && Array.isArray(response.data.data)) {
          setEquipmentList(response.data.data);
        } else {
          setEquipmentList([
            { id: 'eq-1', serialNumber: 'VENT-BSUTH-01', name: 'Hamilton C3 Mechanical Ventilator', category: 'VENTILATOR', status: 'IN_USE', locationWardName: 'Lassa Isolation Ward B (ICU)' },
            { id: 'eq-2', serialNumber: 'VENT-BSUTH-02', name: 'Mindray SV300 Ventilator', category: 'VENTILATOR', status: 'AVAILABLE', locationWardName: 'Lassa Isolation Ward A' },
            { id: 'eq-3', serialNumber: 'PUMP-BSUTH-09', name: 'B.Braun Infusomat Space Pump', category: 'INFUSION_PUMP', status: 'UNDER_MAINTENANCE', locationWardName: 'Biomedical Workshop' },
            { id: 'eq-4', serialNumber: 'MON-BSUTH-04', name: 'Philips Intellivue Patient Monitor', category: 'PATIENT_MONITOR', status: 'AVAILABLE', locationWardName: 'High Dependency Unit (HDU)' },
          ]);
        }
      } catch (e) {
        console.error(e);
      }
    };
    fetchEquipment();
  }, []);

  return (
    <AppShell title="Medical Equipment Catalogue">
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Stethoscope className="h-4 w-4 text-clinical-navy" />
              <span>Critical Medical Equipment Tracker</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Serial Number & Equipment</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead>Location Ward</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {equipmentList.map((eq) => (
                  <TableRow key={eq.id}>
                    <TableCell>
                      <div>
                        <p className="font-bold text-clinical-navy">{eq.name}</p>
                        <p className="text-[10px] font-mono text-gray-500">{eq.serialNumber}</p>
                      </div>
                    </TableCell>
                    <TableCell><Badge variant="neutral">{eq.category}</Badge></TableCell>
                    <TableCell className="text-xs">{eq.locationWardName || 'Central Supply'}</TableCell>
                    <TableCell>
                      <Badge
                        variant={
                          eq.status === 'AVAILABLE'
                            ? 'ready'
                            : eq.status === 'IN_USE'
                            ? 'info'
                            : 'critical'
                        }
                      >
                        {eq.status}
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
