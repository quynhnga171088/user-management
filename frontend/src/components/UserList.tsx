import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import UserModal from './UserModal';

interface User {
    id: number;
    name: string;
    email: string;
    roles: string[]; // Set<Role> comes as array
    active: boolean;
}

const UserList: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentUser, setCurrentUser] = useState<User | null>(null);

    const fetchUsers = async () => {
        try {
            const response = await api.get('/users');
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleDelete = async (id: number) => {
        if (window.confirm('Are you sure?')) {
            try {
                await api.delete(`/users/${id}`);
                fetchUsers();
            } catch (error) {
                console.error('Error deleting user:', error);
            }
        }
    };

    const handleEdit = (user: User) => {
        setCurrentUser(user);
        setIsModalOpen(true);
    };

    const handleAdd = () => {
        setCurrentUser(null);
        setIsModalOpen(true);
    };

    const handleSave = () => {
        setIsModalOpen(false);
        fetchUsers();
    };

    return (
        <div className="container px-4 mx-auto mt-10">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold">User Management</h2>
                <button
                    onClick={handleAdd}
                    className="px-4 py-2 font-bold text-white bg-green-500 rounded hover:bg-green-700"
                >
                    Add User
                </button>
            </div>
            <div className="overflow-x-auto bg-white rounded-lg shadow">
                <table className="w-full table-auto">
                    <thead>
                        <tr className="bg-gray-200 text-left">
                            <th className="px-4 py-2">ID</th>
                            <th className="px-4 py-2">Name</th>
                            <th className="px-4 py-2">Email</th>
                            <th className="px-4 py-2">Roles</th>
                            <th className="px-4 py-2">Active</th>
                            <th className="px-4 py-2">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user) => (
                            <tr key={user.id} className="border-b hover:bg-gray-50">
                                <td className="px-4 py-2">{user.id}</td>
                                <td className="px-4 py-2">{user.name}</td>
                                <td className="px-4 py-2">{user.email}</td>
                                <td className="px-4 py-2">{user.roles.join(', ')}</td>
                                <td className="px-4 py-2">
                                    <span className={`px-2 py-1 text-xs rounded ${user.active ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800'}`}>
                                        {user.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td className="px-4 py-2">
                                    <button
                                        onClick={() => handleEdit(user)}
                                        className="mr-2 text-blue-500 hover:text-blue-700"
                                    >
                                        Edit
                                    </button>
                                    <button
                                        onClick={() => handleDelete(user.id)}
                                        className="text-red-500 hover:text-red-700"
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            {isModalOpen && (
                <UserModal
                    user={currentUser}
                    onClose={() => setIsModalOpen(false)}
                    onSave={handleSave}
                />
            )}
        </div>
    );
};

export default UserList;
