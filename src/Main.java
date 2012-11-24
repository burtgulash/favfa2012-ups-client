import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.TabItem;

public class Main {

	protected Shell shell;
	private TabFolder tabFolder;
	private TabItem activeTabItem;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(592, 500);
		shell.setText("SWT Application");
		shell.setLayout(new RowLayout(SWT.VERTICAL));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmConnection = new MenuItem(menu, SWT.CASCADE);
		mntmConnection.setText("Connection");

		Menu menu_1 = new Menu(mntmConnection);
		mntmConnection.setMenu(menu_1);

		MenuItem mntmConnect = new MenuItem(menu_1, SWT.NONE);
		mntmConnect.setText("Connect to");

		MenuItem mntmDisconnect = new MenuItem(menu_1, SWT.NONE);
		mntmDisconnect.setText("Disconnect");

		MenuItem mntmCloseTab = new MenuItem(menu_1, SWT.NONE);
		mntmCloseTab.setText("Close tab");

		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new RowData(578, 387));

		// LISTENERS
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				activeTabItem = tabFolder.getSelection()[0];
			}
		});

		// Connect listeners
		mntmConnect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				final Login loginDialog = new Login(shell, SWT.NONE);

				boolean ok = loginDialog.open();
				if (ok) {
					TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
					tabItem.setText("---");

					Tab tab = new Tab(tabFolder, SWT.NONE);
					tabItem.setControl(tab);
					activeTabItem = tabItem;

					tab.connection = new Connection(loginDialog.getHost(),
							Integer.parseInt(loginDialog.getPort()));
					if (!tab.connection.connected)
						return;

					activeTabItem.setText(tab.connection.host + ":"
							+ tab.connection.port);
				}
			}
		});

		mntmDisconnect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (activeTabItem == null)
					return;

				Tab tab = (Tab) activeTabItem.getControl();

				if (tab.connection != null) {
					tab.connection.disconnect();
				}
			}
		});

		mntmCloseTab.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (activeTabItem == null)
					return;

				// TODO that simply?
				activeTabItem.dispose();
			}
		});
	}
}
