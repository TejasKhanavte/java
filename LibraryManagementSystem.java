import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;



public class LibraryManagementSystem extends JFrame {

    static class LibraryData implements Serializable {
        List<Book> books = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        List<IssueRecord> issues = new ArrayList<>();
    }

    static class Book implements Serializable {
        private static final long serialVersionUID = 1L;
        int id;
        String title;
        String author;
        int copies;

        Book(int id, String title, String author, int copies) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.copies = copies;
        }

        @Override
        public String toString() {
            return id + " - " + title;
        }
    }

    static class Member implements Serializable {
        private static final long serialVersionUID = 1L;
        int id;
        String name;
        String email;

        Member(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return id + " - " + name;
        }
    }

    static class IssueRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        int id;
        int bookId;
        int memberId;
        LocalDate issueDate;
        LocalDate dueDate;
        LocalDate returnDate; 

        IssueRecord(int id, int bookId, int memberId, LocalDate issueDate, LocalDate dueDate) {
            this.id = id;
            this.bookId = bookId;
            this.memberId = memberId;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
        }
    }

    private static final String DATA_FILE = "library.dat";
    private LibraryData data;

    private DefaultTableModel booksModel;
    private DefaultTableModel membersModel;
    private DefaultTableModel issuesModel;

    private JTable booksTable;
    private JTable membersTable;
    private JTable issuesTable;

    private JTextField searchField;


    private int nextBookId = 1;
    private int nextMemberId = 1;
    private int nextIssueId = 1;

    public LibraryManagementSystem() {
        loadData();

        setTitle("Mini Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Issue / Return", createIssuePanel());
        tabs.addTab("Search", createSearchPanel());

        add(tabs, BorderLayout.CENTER);

        // Save on close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }

    private JPanel createBooksPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        booksModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Copies"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        booksTable = new JTable(booksModel);
        refreshBooksTable();

        JScrollPane sp = new JScrollPane(booksTable);
        p.add(sp, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton addBtn = new JButton("Add Book");
        JButton editBtn = new JButton("Edit Book");
        JButton delBtn = new JButton("Delete Book");
        JButton saveBtn = new JButton("Save Data");

        addBtn.addActionListener(e -> doAddBook());
        editBtn.addActionListener(e -> doEditBook());
        delBtn.addActionListener(e -> doDeleteBook());
        saveBtn.addActionListener(e -> saveData());

        controls.add(addBtn);
        controls.add(editBtn);
        controls.add(delBtn);
        controls.add(saveBtn);

        p.add(controls, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createMembersPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        membersModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        membersTable = new JTable(membersModel);
        refreshMembersTable();

        JScrollPane sp = new JScrollPane(membersTable);
        p.add(sp, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton addBtn = new JButton("Add Member");
        JButton editBtn = new JButton("Edit Member");
        JButton delBtn = new JButton("Delete Member");

        addBtn.addActionListener(e -> doAddMember());
        editBtn.addActionListener(e -> doEditMember());
        delBtn.addActionListener(e -> doDeleteMember());

        controls.add(addBtn);
        controls.add(editBtn);
        controls.add(delBtn);

        p.add(controls, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createIssuePanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        issuesModel = new DefaultTableModel(new Object[]{"IssueID", "BookID", "BookTitle", "MemberID", "MemberName", "IssueDate", "DueDate", "ReturnDate"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        issuesTable = new JTable(issuesModel);
        refreshIssuesTable();

        JScrollPane sp = new JScrollPane(issuesTable);
        p.add(sp, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Book");

        issueBtn.addActionListener(e -> doIssueBook());
        returnBtn.addActionListener(e -> doReturnBook());

        controls.add(issueBtn);
        controls.add(returnBtn);

        p.add(controls, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createSearchPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        JPanel top = new JPanel();
        searchField = new JTextField(30);
        JButton searchBtn = new JButton("Search Books");
        searchBtn.addActionListener(e -> doSearchBooks());
        top.add(new JLabel("Search (title or author):"));
        top.add(searchField);
        top.add(searchBtn);
        p.add(top, BorderLayout.NORTH);

        DefaultTableModel resultsModel = new DefaultTableModel(new Object[]{"ID","Title","Author","Copies"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable resultsTable = new JTable(resultsModel);
        p.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String term = searchField.getText().trim().toLowerCase();
            resultsModel.setRowCount(0);
            for (Book b : data.books) {
                if (b.title.toLowerCase().contains(term) || b.author.toLowerCase().contains(term)) {
                    resultsModel.addRow(new Object[]{b.id, b.title, b.author, b.copies});
                }
            }
        });

        return p;
    }

  
    private void doAddBook() {
        JTextField titleF = new JTextField();
        JTextField authorF = new JTextField();
        JTextField copiesF = new JTextField("1");
        Object[] arr = {"Title:", titleF, "Author:", authorF, "Copies:", copiesF};
        int res = JOptionPane.showConfirmDialog(this, arr, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String title = titleF.getText().trim();
            String author = authorF.getText().trim();
            int copies;
            try {
                copies = Integer.parseInt(copiesF.getText().trim());
                if (copies < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Copies must be a non-negative integer.");
                return;
            }
            Book b = new Book(nextBookId++, title, author, copies);
            data.books.add(b);
            refreshBooksTable();
            saveData();
        }
    }

    private void doEditBook() {
        int row = booksTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a book to edit."); return; }
        int id = (int) booksModel.getValueAt(row, 0);
        Book b = findBookById(id);
        if (b == null) return;
        JTextField titleF = new JTextField(b.title);
        JTextField authorF = new JTextField(b.author);
        JTextField copiesF = new JTextField(String.valueOf(b.copies));
        Object[] arr = {"Title:", titleF, "Author:", authorF, "Copies:", copiesF};
        int res = JOptionPane.showConfirmDialog(this, arr, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int copies = Integer.parseInt(copiesF.getText().trim());
                if (copies < 0) throw new NumberFormatException();
                b.title = titleF.getText().trim();
                b.author = authorF.getText().trim();
                b.copies = copies;
                refreshBooksTable();
                saveData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Copies must be a non-negative integer.");
            }
        }
    }

    private void doDeleteBook() {
        int row = booksTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a book to delete."); return; }
        int id = (int) booksModel.getValueAt(row, 0);
        Book b = findBookById(id);
        if (b == null) return;
     
        for (IssueRecord ir : data.issues) {
            if (ir.bookId == b.id && ir.returnDate == null) {
                JOptionPane.showMessageDialog(this, "Cannot delete: book is currently issued.");
                return;
            }
        }
        int res = JOptionPane.showConfirmDialog(this, "Delete book: " + b.title + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            data.books.remove(b);
            refreshBooksTable();
            saveData();
        }
    }


    private void doAddMember() {
        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();
        Object[] arr = {"Name:", nameF, "Email:", emailF};
        int res = JOptionPane.showConfirmDialog(this, arr, "Add Member", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            Member m = new Member(nextMemberId++, name, email);
            data.members.add(m);
            refreshMembersTable();
            saveData();
        }
    }

    private void doEditMember() {
        int row = membersTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a member to edit."); return; }
        int id = (int) membersModel.getValueAt(row, 0);
        Member m = findMemberById(id);
        if (m == null) return;
        JTextField nameF = new JTextField(m.name);
        JTextField emailF = new JTextField(m.email);
        Object[] arr = {"Name:", nameF, "Email:", emailF};
        int res = JOptionPane.showConfirmDialog(this, arr, "Edit Member", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            m.name = nameF.getText().trim();
            m.email = emailF.getText().trim();
            refreshMembersTable();
            saveData();
        }
    }

    private void doDeleteMember() {
        int row = membersTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a member to delete."); return; }
        int id = (int) membersModel.getValueAt(row, 0);
        Member m = findMemberById(id);
        if (m == null) return;
        // check if member has issued books
        for (IssueRecord ir : data.issues) {
            if (ir.memberId == m.id && ir.returnDate == null) {
                JOptionPane.showMessageDialog(this, "Cannot delete: member has currently issued books.");
                return;
            }
        }
        int res = JOptionPane.showConfirmDialog(this, "Delete member: " + m.name + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            data.members.remove(m);
            refreshMembersTable();
            saveData();
        }
    }

   
    private void doIssueBook() {
        
        if (data.books.isEmpty() || data.members.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Need at least one book and one member to issue.");
            return;
        }
        String bookChoices[] = data.books.stream().map(b -> b.id + " - " + b.title).toArray(String[]::new);
        String memChoices[] = data.members.stream().map(m -> m.id + " - " + m.name).toArray(String[]::new);

        JComboBox<String> bookBox = new JComboBox<>(bookChoices);
        JComboBox<String> memBox = new JComboBox<>(memChoices);
        JTextField daysF = new JTextField("14");
        Object[] arr = {"Select Book:", bookBox, "Select Member:", memBox, "Days to borrow:", daysF};
        int res = JOptionPane.showConfirmDialog(this, arr, "Issue Book", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            int bookId = Integer.parseInt(((String)bookBox.getSelectedItem()).split(" - ")[0]);
            int memId = Integer.parseInt(((String)memBox.getSelectedItem()).split(" - ")[0]);
            int days;
            try {
                days = Integer.parseInt(daysF.getText().trim());
                if (days <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Days must be a positive integer.");
                return;
            }
            Book b = findBookById(bookId);
            if (b == null) return;
            // check copies available (count currently issued)
            long issuedCount = data.issues.stream().filter(ir -> ir.bookId == b.id && ir.returnDate == null).count();
            if (issuedCount >= b.copies) {
                JOptionPane.showMessageDialog(this, "No copies available to issue.");
                return;
            }
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(days);
            IssueRecord ir = new IssueRecord(nextIssueId++, b.id, memId, issueDate, dueDate);
            data.issues.add(ir);
            refreshIssuesTable();
            saveData();
            JOptionPane.showMessageDialog(this, "Book issued. Due: " + dueDate.toString());
        }
    }

    private void doReturnBook() {
        int row = issuesTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an issue record to return."); return; }
        int issueId = (int) issuesModel.getValueAt(row, 0);
        IssueRecord ir = findIssueById(issueId);
        if (ir == null) return;
        if (ir.returnDate != null) {
            JOptionPane.showMessageDialog(this, "This book is already returned.");
            return;
        }
        LocalDate ret = LocalDate.now();
        ir.returnDate = ret;
       
        long overdueDays = ChronoUnit.DAYS.between(ir.dueDate, ret);
        long fine = Math.max(0, overdueDays) * 10;
        refreshIssuesTable();
        saveData();
        if (fine > 0) {
            JOptionPane.showMessageDialog(this, "Book returned. Fine: " + fine + " (overdue " + Math.max(0, overdueDays) + " days).");
        } else {
            JOptionPane.showMessageDialog(this, "Book returned. Thank you!");
        }
    }

   
    private Book findBookById(int id) {
        for (Book b : data.books) if (b.id == id) return b;
        return null;
    }

    private Member findMemberById(int id) {
        for (Member m : data.members) if (m.id == id) return m;
        return null;
    }

    private IssueRecord findIssueById(int id) {
        for (IssueRecord ir : data.issues) if (ir.id == id) return ir;
        return null;
    }

    private void refreshBooksTable() {
        booksModel.setRowCount(0);
        for (Book b : data.books) {
            booksModel.addRow(new Object[]{b.id, b.title, b.author, b.copies});
            nextBookId = Math.max(nextBookId, b.id + 1);
        }
    }

    private void refreshMembersTable() {
        membersModel.setRowCount(0);
        for (Member m : data.members) {
            membersModel.addRow(new Object[]{m.id, m.name, m.email});
            nextMemberId = Math.max(nextMemberId, m.id + 1);
        }
    }

    private void refreshIssuesTable() {
        issuesModel.setRowCount(0);
        for (IssueRecord ir : data.issues) {
            Book b = findBookById(ir.bookId);
            Member m = findMemberById(ir.memberId);
            issuesModel.addRow(new Object[]{
                    ir.id,
                    ir.bookId,
                    (b != null ? b.title : "Unknown"),
                    ir.memberId,
                    (m != null ? m.name : "Unknown"),
                    ir.issueDate != null ? ir.issueDate.toString() : "",
                    ir.dueDate != null ? ir.dueDate.toString() : "",
                    ir.returnDate != null ? ir.returnDate.toString() : ""
            });
            nextIssueId = Math.max(nextIssueId, ir.id + 1);
        }
    }

    private void doSearchBooks() {
      
    }

  
    private void loadData() {
        File f = new File(DATA_FILE);
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                data = (LibraryData) ois.readObject();
               
                for (Book b : data.books) nextBookId = Math.max(nextBookId, b.id + 1);
                for (Member m : data.members) nextMemberId = Math.max(nextMemberId, m.id + 1);
                for (IssueRecord ir : data.issues) nextIssueId = Math.max(nextIssueId, ir.id + 1);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load data, starting fresh.");
                data = new LibraryData();
            }
        } else {
            data = new LibraryData();
            
            Book b1 = new Book(nextBookId++, "Effective Java", "Joshua Bloch", 3);
            Book b2 = new Book(nextBookId++, "Head First Java", "Kathy Sierra", 2);
            data.books.add(b1);
            data.books.add(b2);
            Member m1 = new Member(nextMemberId++, "Alice", "alice@example.com");
            Member m2 = new Member(nextMemberId++, "Bob", "bob@example.com");
            data.members.add(m1);
            data.members.add(m2);
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryManagementSystem app = new LibraryManagementSystem();
            app.setVisible(true);
        });
    }
}
