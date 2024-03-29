import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class Tab extends Composite {
	private Text text;
	private List usersList;
	private StyledText chatWindow;
	private Label lblConnectionInfo;

	private UsersUpdater usersUpdater;
	private ChatUpdater chatUpdater;
	public Connection connection;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public Tab(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);

		lblConnectionInfo = new Label(this, SWT.NONE);
		lblConnectionInfo.setAlignment(SWT.CENTER);
		GridData gd_lblConnectionInfo = new GridData(SWT.CENTER, SWT.CENTER,
				true, false, 1, 1);
		gd_lblConnectionInfo.heightHint = 26;
		gd_lblConnectionInfo.widthHint = 461;
		lblConnectionInfo.setLayoutData(gd_lblConnectionInfo);
		lblConnectionInfo.setText("Connection Info");

		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1);
		gd_composite.heightHint = 285;
		composite.setLayoutData(gd_composite);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);

		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1);
		gd_composite_1.widthHint = 332;
		gd_composite_1.heightHint = 267;
		composite_1.setLayoutData(gd_composite_1);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);

		chatWindow = new StyledText(composite_1, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_chatWindow = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1);
		gd_chatWindow.widthHint = 305;
		gd_chatWindow.heightHint = 214;
		chatWindow.setLayoutData(gd_chatWindow);
		chatWindow.setWordWrap(true);
		chatWindow.setWrapIndent(20);

		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1);
		gd_composite_2.heightHint = 63;
		composite_2.setLayoutData(gd_composite_2);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.verticalSpacing = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.horizontalSpacing = 0;
		gl_composite_2.marginHeight = 0;
		composite_2.setLayout(gl_composite_2);

		text = new Text(composite_2, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_text.widthHint = 230;
		gd_text.heightHint = 44;
		text.setLayoutData(gd_text);

		Button btnSend = new Button(composite_2, SWT.NONE);
		GridData gd_btnSend = new GridData(SWT.FILL, SWT.FILL, false, true, 1,
				1);
		gd_btnSend.widthHint = 68;
		gd_btnSend.heightHint = 62;
		btnSend.setLayoutData(gd_btnSend);
		btnSend.setText("Send");

		usersList = new List(composite, SWT.BORDER | SWT.MULTI);
		GridData gd_usersList = new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1);
		gd_usersList.widthHint = 148;
		usersList.setLayoutData(gd_usersList);

		usersUpdater = new UsersUpdater();
		usersUpdater.start();
		chatUpdater = new ChatUpdater();
		chatUpdater.start();
		text.setFocus();

		btnSend.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (usersList.getSelectionCount() > 0)
					send_private();
				else
					send_all();
			}
		});

		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR)
					send_all();
			}
		});

		chatWindow.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				chatWindow.setTopIndex(chatWindow.getLineCount() - 1);
			}
		});

		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				connection.disconnect();

				usersUpdater.end();
				chatUpdater.end();
			}
		});
	}

	protected void send_all() {
		if (connection.connected()) {
			String message = text.getText().trim();
			text.setText("");
			text.setFocus();
			if ("".equals(message))
				return;

			String line = connection.getLoginName() + ": " + message;

			addLine(line, false);
			connection.send("ALL_MSG " + message);
		}
	}

	protected void send_private() {
		if (connection.connected()) {
			String message = text.getText().trim();
			text.setText("");
			text.setFocus();
			if ("".equals(message))
				return;
			
			String[] recipients = usersList.getSelection();
			for (String recipient : recipients) {
				String line = connection.getLoginName() + " -> " + recipient
						+ ": " + message;
				addLine(line, true);

				connection.send("PRIV_MSG " + recipient + " " + message);

			}
		}
	}

	protected void addLine(final String line, final boolean bold) {
		final StyleRange sr = new StyleRange();
		sr.length = line.length();
		sr.fontStyle = SWT.BOLD;

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String current = chatWindow.getText();

				sr.start = current.length();
				if ("".equals(current))
					chatWindow.insert(line);
				else {
					chatWindow.append("\n" + line);
					sr.start++;
				}

				if (bold)
					chatWindow.setStyleRange(sr);
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected void updateStatus() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (lblConnectionInfo.isDisposed())
					return;
				
				if (connection.connected())
					lblConnectionInfo.setText("Connected to: " + connection.getLoginName() + "@"
							+ connection.connectionInfo());
				else
					lblConnectionInfo.setText("Disconnected");
			}
		});
	}

	public void updateTab() {
		updateStatus();
		usersUpdater.update();
		chatUpdater.update();
	}

	private class UsersUpdater extends Thread {
		private boolean doRun = true;
		private final int USERS_UPDATE_INTERVAL = 5000;

		public void end() {
			doRun = false;
			interrupt();
		}

		public synchronized void update() {
			if (connection != null && connection.connected()) {
				synchronized (connection) {
					connection.send("USERS");
				}
			}
		}

		public void run() {
			while (doRun) {
				update();

				try {
					Thread.sleep(USERS_UPDATE_INTERVAL);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private class ChatUpdater extends Thread {
		private boolean doRun = true;
		private final long MSG_UPDATE_INTERVAL = 131;

		public void end() {
			doRun = false;
			interrupt();
		}

		public void run() {
			while (doRun) {
				update();

				try {
					Thread.sleep(MSG_UPDATE_INTERVAL);
				} catch (InterruptedException e) {
				}
			}
		}

		public synchronized void update() {
			String msg = null;

			if (connection != null && connection.connected()) {
				try {
					synchronized (connection) {
						try {
							msg = connection.recv();
						} catch (SocketTimeoutException e) {
							return;
						}
					}

					// Server down
					if (msg == null) {
						connection.disconnect();
						connection.reconnect();
					}
					updateStatus();

					Response r = new Response(msg);
					if (!r.isValid())
						return;

					if (r.isUSERS()) {
						StringTokenizer izer = new StringTokenizer(r.getData());

						final ArrayList<String> sorted = new ArrayList<String>();
						while (izer.hasMoreTokens())
							sorted.add(izer.nextToken());
						Collections.sort(sorted);

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								String[] selected = usersList.getSelection();

								usersList.removeAll();
								for (String u : sorted)
									usersList.add(u);

								usersList.setSelection(selected);
							}
						});
					} else if (r.isALL_MSG()) {
						String data = r.getData();
						int c = 0;
						while (data.charAt(c++) != ' ')
							;
						String from = data.substring(0, c - 1);
						String text = data.substring(c);
						String line = from + ": " + text;

						addLine(line, false);
					} else if (r.isPRIV_MSG()) {
						String data = r.getData();
						int c = 0;
						while (data.charAt(c++) != ' ')
							;
						String from = data.substring(0, c - 1);
						String me = connection.getLoginName();
						String text = data.substring(c);
						String line = from + " -> " + me + ": " + text;

						addLine(line, true);
					} else {
						System.err.println("Invalid response type");
					}

				} catch (IOException e) {
					System.err.println("Received input/Output error.");
				}
			}
		}
	}
}
