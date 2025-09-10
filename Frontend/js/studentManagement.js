document.addEventListener("DOMContentLoaded", () => {
    const studentTableBody = document.getElementById("studentTableBody");

    // Sample students (replace with backend API in real app)
    let students = JSON.parse(localStorage.getItem("students")) || [
       
    ];

    // Display students
    function displayStudents() {
        studentTableBody.innerHTML = '';
        students.forEach(student => {
            let row = document.createElement('tr');
            row.innerHTML = `
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.email}</td>
                <td>${student.phone}</td>
                <td>${student.date}</td>
                <td>
                    <button class="btn edit" onclick="editStudent('${student.id}')">Edit</button>
                    <button class="btn delete" onclick="deleteStudent('${student.id}')">Delete</button>
                </td>
            `;
            studentTableBody.appendChild(row);
        });
    }

    displayStudents();

    // Edit student
    window.editStudent = function(id) {
        const student = students.find(s => s.id === id);
        if (!student) return;
        const newName = prompt("Edit full name:", student.name);
        const newEmail = prompt("Edit email:", student.email);
        const newPhone = prompt("Edit phone:", student.phone);

        if(newName) student.name = newName;
        if(newEmail) student.email = newEmail;
        if(newPhone) student.phone = newPhone;

        localStorage.setItem("students", JSON.stringify(students));
        displayStudents();
    }

    // Delete student
    window.deleteStudent = function(id) {
        if(!confirm("Are you sure you want to delete this student?")) return;
        students = students.filter(s => s.id !== id);
        localStorage.setItem("students", JSON.stringify(students));
        displayStudents();
    }
});
