import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { UserSquare2 } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Staff } from '../types';

export const StaffShifts: React.FC = () => {
  const [staffList, setStaffList] = useState<Staff[]>([]);

  useEffect(() => {
    const fetchStaff = async () => {
      try {
        const response = await apiClient.get('/staff');
        if (response.data.success && Array.isArray(response.data.data)) {
          setStaffList(response.data.data);
        } else {
          setStaffList([
            { id: 's-1', staffNumber: 'BSUTH-STF-012', fullName: 'Dr. Terver Gbande', role: 'DOCTOR', unit: 'Lassa Isolation Unit', contactNumber: '+234 803 123 4567', status: 'ACTIVE' },
            { id: 's-2', staffNumber: 'BSUTH-STF-045', fullName: 'Nurse Mercy Kever', role: 'NURSE', unit: 'Lassa Isolation Ward A', contactNumber: '+234 802 987 6543', status: 'ACTIVE' },
            { id: 's-3', staffNumber: 'BSUTH-STF-078', fullName: 'Nurse Moses Iorbee', role: 'NURSE', unit: 'Lassa Isolation Ward B (ICU)', contactNumber: '+234 805 444 3322', status: 'ACTIVE' },
            { id: 's-4', staffNumber: 'BSUTH-STF-102', fullName: 'John Tyokyaa', role: 'CLEANER', unit: 'Decontamination Unit', contactNumber: '+234 807 111 2233', status: 'ACTIVE' },
          ]);
        }
      } catch (e) {
        console.error(e);
      }
    };
    fetchStaff();
  }, []);

  return (
    <AppShell title="Staff & Shift Roster">
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <UserSquare2 className="h-4 w-4 text-clinical-navy" />
              <span>Clinical Personnel Roster</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Staff Member</TableHead>
                  <TableHead>Role</TableHead>
                  <TableHead>Assigned Unit</TableHead>
                  <TableHead>Contact</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {staffList.map((stf) => (
                  <TableRow key={stf.id}>
                    <TableCell>
                      <div>
                        <p className="font-bold text-clinical-navy">{stf.fullName}</p>
                        <p className="text-[10px] font-mono text-gray-500">{stf.staffNumber}</p>
                      </div>
                    </TableCell>
                    <TableCell><Badge variant="neutral">{stf.role}</Badge></TableCell>
                    <TableCell className="text-xs">{stf.unit}</TableCell>
                    <TableCell className="font-mono text-xs">{stf.contactNumber}</TableCell>
                    <TableCell><Badge variant="ready">{stf.status}</Badge></TableCell>
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
