import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;

/**
 * ExcelAdapter enables Copy-Paste Clipboard functionality on JTables. The
 * clipboard data format used by the adapter is compatible with the clipboard
 * format used by Excel. This provides for clipboard interoperability between
 * enabled JTables and Excel.
 */
public class ExcelAdapter implements ActionListener {
   private Clipboard system;
   private StringSelection stsel;
   private JTable table;

   public ExcelAdapter(JTable table) {
      this.table = table;
      KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
      this.table.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
      system = Toolkit.getDefaultToolkit().getSystemClipboard();
   }

   public JTable getJTable() {
      return table;
   }

   public void setJTable(JTable table) {
      this.table = table;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().compareTo("Copy") == 0) {
         System.out.println("copy");
         StringBuffer sbf = new StringBuffer();

         // Check to ensure we have selected only a contiguous block of
         // cells
         int numcols = table.getSelectedColumnCount();
         int numrows = table.getSelectedRowCount();
         int[] rowsselected = table.getSelectedRows();
         int[] colsselected = table.getSelectedColumns();
         if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0]
               && numrows == rowsselected.length)
               && (numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0]
                     && numcols == colsselected.length))) {
            JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection",
                  JOptionPane.ERROR_MESSAGE);
            return;
         }

         for (int i = 0; i < numrows; i++) {
            for (int j = 0; j < numcols; j++) {
               sbf.append(table.getValueAt(rowsselected[i], colsselected[j]));
               if (j < numcols - 1)
                  sbf.append("\t");
            }
            sbf.append("\n");
         }
         stsel = new StringSelection(sbf.toString());
         system = Toolkit.getDefaultToolkit().getSystemClipboard();
         system.setContents(stsel, stsel);
      }
   }
}