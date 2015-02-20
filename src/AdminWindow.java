import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class AdminWindow {

	/** Textfeld für Adminausgaben */
	JTextPane printAreaAdminBox;
	/** Scrollpane für Textfeld */
	JScrollPane textScroll;
	//AdminWindowFrame
	public JFrame adminWINDOW;
	
	/**
	 * Konstruktor von AdminWindow.
	 * Zeichnet auschließlich das AdminWindow.
	 */
	public AdminWindow() {
		drawAdminWindow();
	}
	/**
	 * Zeichne Adminbox
	 */
	private void drawAdminWindow() {
		adminWINDOW = new JFrame("AdminBox");
		adminWINDOW.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		printAreaAdminBox = new JTextPane();
		printAreaAdminBox.setContentType("text/html");
		printAreaAdminBox.setEditorKit(new HTMLEditorKit());
		printAreaAdminBox.setSize(300, 200);
		printAreaAdminBox.setVisible(true);
		printAreaAdminBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)printAreaAdminBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textScroll = new JScrollPane(printAreaAdminBox);
		textScroll.setVisible(true);
		adminWINDOW.add(textScroll);
		adminWINDOW.setSize(300, 200);
		adminWINDOW.setLocation(725, 265);
		adminWINDOW.setResizable(false);
		adminWINDOW.setVisible(true);
	}
	
	/**
	 * Zeigt Adminausgaen in der AdminBox an
	 * @param text
	 */
	public void showMessageAdmin(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						Document doc  = printAreaAdminBox.getDocument();
						HTMLEditorKit htmlEdit = new HTMLEditorKit();
						try {
							htmlEdit.insertHTML((HTMLDocument) doc, doc.getLength(), text, 0,0, null);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}			
					}
				}
			);
	}
}
