document.addEventListener("DOMContentLoaded", () => {
    const courseForm = document.getElementById("courseForm");
    const courseList = document.getElementById("courseList");

    if(courseForm) {
        courseForm.addEventListener("submit", function(e) {
            e.preventDefault();
            const courseName = document.getElementById("courseName").value;

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${courseName}</td>
                <td>
                    <button class="btn edit">Edit</button>
                    <button class="btn delete">Delete</button>
                </td>
            `;
            courseList.appendChild(row);
            this.reset();
        });
    }

    if(courseList) {
        courseList.addEventListener("click", function(e) {
            if(e.target.classList.contains("delete")) {
                e.target.closest("tr").remove();
            }
            if(e.target.classList.contains("edit")) {
                const row = e.target.closest("tr");
                const nameCell = row.querySelector("td:first-child");
                const newName = prompt("Edit course name:", nameCell.textContent);
                if(newName) nameCell.textContent = newName;
            }
        });
    }
});
document.addEventListener("DOMContentLoaded", () => {
    // Instructor Management
    const instructorForm = document.getElementById("instructorForm");
    const instructorList = document.getElementById("instructorList");

    if (instructorForm) {
        instructorForm.addEventListener("submit", function(e) {
            e.preventDefault();

            const name = document.getElementById("instructorName").value;
            const email = document.getElementById("instructorEmail").value;
            const course = document.getElementById("instructorCourse").value;

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${name}</td>
                <td>${email}</td>
                <td>${course}</td>
                <td>
                    <button class="btn edit">Edit</button>
                    <button class="btn delete">Delete</button>
                </td>
            `;
            instructorList.appendChild(row);
            this.reset();
        });
    }

    if (instructorList) {
        instructorList.addEventListener("click", function(e) {
            if (e.target.classList.contains("delete")) {
                e.target.closest("tr").remove();
            }
            if (e.target.classList.contains("edit")) {
                const row = e.target.closest("tr");
                const nameCell = row.querySelector("td:nth-child(1)");
                const emailCell = row.querySelector("td:nth-child(2)");
                const courseCell = row.querySelector("td:nth-child(3)");

                const newName = prompt("Edit instructor name:", nameCell.textContent);
                const newEmail = prompt("Edit email:", emailCell.textContent);
                const newCourse = prompt("Edit course:", courseCell.textContent);

                if (newName) nameCell.textContent = newName;
                if (newEmail) emailCell.textContent = newEmail;
                if (newCourse) courseCell.textContent = newCourse;
            }
        });
    }
});
