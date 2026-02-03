import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

interface UserModalProps {
    user: any;
    onClose: () => void;
    onSave: () => void;
}

const UserModal: React.FC<UserModalProps> = ({ user, onClose, onSave }) => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        roles: [] as string[],
        active: true
    });

    useEffect(() => {
        if (user) {
            setFormData({
                name: user.name,
                email: user.email,
                password: '', // Don't show password
                roles: user.roles,
                active: user.active
            });
        }
    }, [user]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleRoleChange = (role: string) => {
        setFormData(prev => {
            const roles = prev.roles.includes(role)
                ? prev.roles.filter(r => r !== role)
                : [...prev.roles, role];
            return { ...prev, roles };
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (user) {
                await api.put(`/users/${user.id}`, formData);
            } else {
                await api.post('/users', formData);
            }
            onSave();
        } catch (error) {
            console.error('Error saving user:', error);
            alert('Failed to save user');
        }
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
            <div className="w-full max-w-md p-6 bg-white rounded-lg shadow-lg">
                <h2 className="mb-4 text-xl font-bold">{user ? 'Edit User' : 'Add User'}</h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block mb-1 text-sm font-bold">Name</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="w-full px-3 py-2 border rounded"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block mb-1 text-sm font-bold">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            className="w-full px-3 py-2 border rounded"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block mb-1 text-sm font-bold">Password {user && '(Leave blank to keep current)'}</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            className="w-full px-3 py-2 border rounded"
                            required={!user}
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block mb-1 text-sm font-bold">Roles</label>
                        <div className="flex gap-4">
                            {['ADMIN', 'USER', 'GUEST'].map(role => (
                                <label key={role} className="flex items-center">
                                    <input
                                        type="checkbox"
                                        checked={formData.roles.includes(role)}
                                        onChange={() => handleRoleChange(role)}
                                        className="mr-2"
                                    />
                                    {role}
                                </label>
                            ))}
                        </div>
                    </div>
                    <div className="mb-4">
                        <label className="flex items-center">
                            <input
                                type="checkbox"
                                name="active"
                                checked={formData.active}
                                onChange={handleChange}
                                className="mr-2"
                            />
                            <span className="text-sm font-bold">Active</span>
                        </label>
                    </div>
                    <div className="flex justify-end gap-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-gray-600 bg-gray-200 rounded hover:bg-gray-300"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 text-white bg-blue-500 rounded hover:bg-blue-700"
                        >
                            Save
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default UserModal;
