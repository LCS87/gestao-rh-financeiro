import React from 'react';
import { Card, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';

interface CrudLayoutProps {
  title: string;
  children: React.ReactNode;
  onAdd?: () => void;
  addText?: string;
}

export default function CrudLayout({
  title,
  children,
  onAdd,
  addText = 'Adicionar',
}: CrudLayoutProps) {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-white">{title}</h1>
          <p className="text-slate-300">Gerenciamento de {title.toLowerCase()}</p>
        </div>
        {onAdd && <Button onClick={onAdd}>{addText}</Button>}
      </div>

      <Card>
        <CardContent className="p-6">{children}</CardContent>
      </Card>
    </div>
  );
}
