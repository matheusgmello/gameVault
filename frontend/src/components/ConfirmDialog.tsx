import React from 'react';
import Modal from './Modal';

interface ConfirmDialogProps {
  isOpen: boolean;
  title: string;
  description: string;
  confirmLabel: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
  danger?: boolean;
}

const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  isOpen,
  title,
  description,
  confirmLabel,
  cancelLabel = 'Cancelar',
  onConfirm,
  onCancel,
  danger = false,
}) => (
  <Modal isOpen={isOpen} onClose={onCancel} title={title}>
    <div className="confirm-dialog">
      <p>{description}</p>
      <div className="form-actions">
        <button type="button" className="btn-cancel" onClick={onCancel}>{cancelLabel}</button>
        <button type="button" className={danger ? 'btn-danger' : 'btn-save'} onClick={onConfirm}>{confirmLabel}</button>
      </div>
    </div>
  </Modal>
);

export default ConfirmDialog;
