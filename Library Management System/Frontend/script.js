const bookForm =
    document.getElementById("bookForm");

const bookList =
    document.getElementById("bookList");

const message =
    document.getElementById("message");

const search =
    document.getElementById("search");


async function loadBooks() {

    try {

        const response =
            await fetch("http://localhost:8080/api/books");

        if (!response.ok) {
            throw new Error("Unable to load books");
        }

        const books =
            await response.json();

        displayBooks(books);

    } catch (error) {

        showMessage(
            "Unable to connect to Java server.",
            "error"
        );
    }
}


function displayBooks(books) {

    bookList.innerHTML = "";

    books.forEach(book => {

        const row =
            document.createElement("tr");

        let actionButton = "";

        if (book.status === "Available") {

            actionButton = `
                <button
                    class="issue-btn"
                    onclick="issueBook(${book.id})">
                    Issue
                </button>
            `;

        } else {

            actionButton = `
                <button
                    class="return-btn"
                    onclick="returnBook(${book.id})">
                    Return
                </button>
            `;
        }

        row.innerHTML = `
            <td>${book.id}</td>

            <td>${escapeHtml(book.title)}</td>

            <td>${escapeHtml(book.author)}</td>

            <td>${escapeHtml(book.category)}</td>

            <td>${book.status}</td>

            <td>
                ${actionButton}

                <button
                    class="delete-btn"
                    onclick="deleteBook(${book.id})">
                    Delete
                </button>
            </td>
        `;

        bookList.appendChild(row);
    });
}


bookForm.addEventListener(
    "submit",
    async function(event) {

        event.preventDefault();

        const title =
            document.getElementById("title")
            .value.trim();

        const author =
            document.getElementById("author")
            .value.trim();

        const category =
            document.getElementById("category")
            .value.trim();

        if (!title || !author || !category) {

            showMessage(
                "Please fill all fields.",
                "error"
            );

            return;
        }

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/books",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            title,
                            author,
                            category
                        })
                    }
                );

            const data =
                await response.json();

            if (!response.ok) {

                throw new Error(
                    data.error ||
                    "Unable to add book"
                );
            }

            bookForm.reset();

            showMessage(
                "Book added successfully!",
                "success"
            );

            loadBooks();

        } catch (error) {

            showMessage(
                error.message,
                "error"
            );
        }
    }
);


async function issueBook(id) {

    try {

        const response =
            await fetch(
                `http://localhost:8080/api/books/${id}/issue`,
                {
                    method: "PUT"
                }
            );

        const data =
            await response.json();

        if (!response.ok) {
            throw new Error(data.error);
        }

        showMessage(
            "Book issued successfully!",
            "success"
        );

        loadBooks();

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}


async function returnBook(id) {

    try {

        const response =
            await fetch(
                `http://localhost:8080/api/books/${id}/return`,
                {
                    method: "PUT"
                }
            );

        const data =
            await response.json();

        if (!response.ok) {
            throw new Error(data.error);
        }

        showMessage(
            "Book returned successfully!",
            "success"
        );

        loadBooks();

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}


async function deleteBook(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this book?"
        );

    if (!confirmed) {
        return;
    }

    try {

        const response =
            await fetch(
                `http://localhost:8080/api/books/${id}`,
                {
                    method: "DELETE"
                }
            );

        const data =
            await response.json();

        if (!response.ok) {
            throw new Error(data.error);
        }

        showMessage(
            "Book deleted successfully!",
            "success"
        );

        loadBooks();

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}


search.addEventListener(
    "input",
    async function() {

        const value =
            search.value.toLowerCase();

        const response =
            await fetch(
                "http://localhost:8080/api/books"
            );

        const books =
            await response.json();

        const filtered =
            books.filter(book =>
                book.title
                    .toLowerCase()
                    .includes(value) ||

                book.author
                    .toLowerCase()
                    .includes(value) ||

                book.category
                    .toLowerCase()
                    .includes(value)
            );

        displayBooks(filtered);
    }
);


function showMessage(text, type) {

    message.textContent = text;

    message.className = type;

    setTimeout(() => {

        message.textContent = "";

        message.className = "";

    }, 3000);
}


function escapeHtml(value) {

    const div =
        document.createElement("div");

    div.textContent = value;

    return div.innerHTML;
}


loadBooks();