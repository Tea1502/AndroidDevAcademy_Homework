
abstract class Person(
    val name: String
) {
    abstract fun info(): String
}




interface Readable {
    fun read(): String
}


class Book(
    val title: String,
    val author: String,
    var available: Boolean = true,
    var pages: Int?
) : Readable {

    override fun read(): String {
        return "Citam $title"
    }
}

class Student(
    name: String,
    var year: Int
) : Person(name) {


    private val books = mutableListOf<Book>()

    override fun info(): String {
        return "Student $name, godina $year"
    }

    fun borrow(book: Book): Boolean {
        if (book.available) {
            books.add(book)
            book.available = false
            return true
        }
        return false
    }

    fun returnBook(book: Book) {
        books.remove(book)
        book.available = true
    }

    fun printBooks() {
        println("Knjige od $name:")
        for (b in books) {
            println(b.title)
        }
    }
}


class Library(
    val name: String
) {

    private val books = mutableListOf<Book>()
    private val students = mutableListOf<Student>()

    fun addBook(book: Book) {
        books.add(book)
    }

    fun addStudent(student: Student) {
        students.add(student)
    }

    fun availableBooks(): List<Book> {
        return books.filter { it.available }
    }

    fun booksByAuthor(author: String): List<Book> {
        return books.filter { it.author == author }
    }

    fun totalPages(): Int {

        return books.mapNotNull { it.pages }.sum()
    }

    fun printAll() {
        for (b in books) {
            println(b.title + " - " + b.author)
        }
    }
}


fun doSomethingWithBooks(list: List<Book>, f: (Book) -> Unit) {
    for (b in list) {
        f(b)
    }
}

fun main() {

    val lib = Library("Moja knjiznica")

    val b1 = Book("Ana Karenjina", "Tolstoj", pages = 200)
    val b2 = Book("Planine", "Zoranic", pages = 100)
    val b3 = Book("Pinokio", "Collodi", pages = null)

    val s1 = Student("Ana", 2)
    val s2 = Student("Ivan", 1)

    lib.addBook(b1)
    lib.addBook(b2)
    lib.addBook(b3)

    lib.addStudent(s1)
    lib.addStudent(s2)

    println("Posudba:")
    println(s1.borrow(b1)) // true
    println(s2.borrow(b1)) // false

    println("\nDostupne knjige:")
    val available = lib.availableBooks()
    for (b in available) {
        println(b.title)
    }

    println("\nSve knjige:")
    lib.printAll()

    println("\nUkupno stranica:")
    println(lib.totalPages())

    println("\nFunkcija viseg reda:")
    doSomethingWithBooks(available) {
        println("Obrada: " + it.title)
    }

    println("\nInfo o studentu:")
    println(s1.info())

    s1.printBooks()
}