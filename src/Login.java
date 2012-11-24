import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;


public class Login extends Dialog {

	protected Object result;
	protected Shell shlLogin;
	private Label lblLoginName;
	private Label lblHost;
	private Text hostText;
	private Text portText;
	private Text loginNameText;
	
	private Button btnCancel;
	private Button btnConnect;
	
	
	private String host;
	private String port;
	private String loginName;
	boolean ok;
	

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Login(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}
	
	public String getHost() {
		return host;
	}
	
	public String getPort() {
		return port;
	}
	
	public String getLoginName() {
		return loginName;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public boolean open() {
		createContents();
		createListeners();
		shlLogin.open();
		shlLogin.layout();
		Display display = getParent().getDisplay();
		
		while (!shlLogin.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return ok;
	}
	
	private void createListeners() {
		SelectionAdapter listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ok = event.widget == btnConnect;
				if (ok) {
					host = hostText.getText();
					port = portText.getText();
					loginName = loginNameText.getText();
				}
				shlLogin.close();
			}
		};
		btnCancel.addSelectionListener(listener);
		btnConnect.addSelectionListener(listener);
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlLogin = new Shell(getParent(), getStyle());
		shlLogin.setSize(450, 300);
		shlLogin.setText("Login");
		shlLogin.setLayout(new FormLayout());
		
		Label lblPort = new Label(shlLogin, SWT.NONE);
		FormData fd_lblPort = new FormData();
		fd_lblPort.left = new FormAttachment(0, 10);
		lblPort.setLayoutData(fd_lblPort);
		lblPort.setText("Port:");
		
		lblLoginName = new Label(shlLogin, SWT.NONE);
		fd_lblPort.bottom = new FormAttachment(lblLoginName, -16);
		FormData fd_lblLoginName = new FormData();
		fd_lblLoginName.top = new FormAttachment(0, 100);
		fd_lblLoginName.left = new FormAttachment(0, 10);
		lblLoginName.setLayoutData(fd_lblLoginName);
		lblLoginName.setText("Login name:");
		
		lblHost = new Label(shlLogin, SWT.NONE);
		FormData fd_lblHost = new FormData();
		fd_lblHost.bottom = new FormAttachment(lblPort, -16);
		fd_lblHost.left = new FormAttachment(0, 10);
		lblHost.setLayoutData(fd_lblHost);
		lblHost.setText("Host:");
		
		hostText = new Text(shlLogin, SWT.BORDER);
		hostText.setText("localhost");
		FormData fd_host = new FormData();
		fd_host.top = new FormAttachment(0, 10);
		fd_host.right = new FormAttachment(100, -32);
		fd_host.left = new FormAttachment(100, -269);
		hostText.setLayoutData(fd_host);
		
		portText = new Text(shlLogin, SWT.BORDER);
		portText.setText("1234");
		FormData fd_port = new FormData();
		fd_port.bottom = new FormAttachment(lblPort, 0, SWT.BOTTOM);
		fd_port.left = new FormAttachment(hostText, 0, SWT.LEFT);
		fd_port.right = new FormAttachment(hostText, 0, SWT.RIGHT);
		portText.setLayoutData(fd_port);
		
		loginNameText = new Text(shlLogin, SWT.BORDER);
		loginNameText.setText("gusta");
		FormData fd_loginName = new FormData();
		fd_loginName.right = new FormAttachment(hostText, 0, SWT.RIGHT);
		fd_loginName.top = new FormAttachment(portText, 6);
		fd_loginName.left = new FormAttachment(lblLoginName, 45);
		loginNameText.setLayoutData(fd_loginName);
		
		btnConnect = new Button(shlLogin, SWT.NONE);
		FormData fd_btnConnect = new FormData();
		fd_btnConnect.bottom = new FormAttachment(100, -10);
		fd_btnConnect.right = new FormAttachment(hostText, 0, SWT.RIGHT);
		btnConnect.setLayoutData(fd_btnConnect);
		btnConnect.setText("Connect");
		
		btnCancel = new Button(shlLogin, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.top = new FormAttachment(btnConnect, 0, SWT.TOP);
		fd_btnCancel.right = new FormAttachment(btnConnect, -6);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");

		
		shlLogin.setDefaultButton(btnConnect);
	}
}
