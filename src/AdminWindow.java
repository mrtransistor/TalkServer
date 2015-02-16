import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class AdminWindow {

	/** Textfeld für Adminausgaben */
	JTextArea printAreaAdminBox;
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
		printAreaAdminBox = new JTextArea();
		printAreaAdminBox.setSize(300, 200);
		printAreaAdminBox.setVisible(true);
		printAreaAdminBox.setEditable(false);
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
						printAreaAdminBox.append("\n" + text);
					}
				}
			);
	}
}
