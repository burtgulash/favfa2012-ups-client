import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

		Label lblConnectionInfo = new Label(this, SWT.NONE);
		lblConnectionInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblConnectionInfo.setAlignment(SWT.CENTER);
		lblConnectionInfo.setText("Connection Info");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);

		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite_1.widthHint = 332;
		gd_composite_1.heightHint = 267;
		composite_1.setLayoutData(gd_composite_1);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);

		chatWindow = new StyledText(composite_1, SWT.BORDER);
		GridData gd_chatWindow = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_chatWindow.widthHint = 305;
		gd_chatWindow.heightHint = 214;
		chatWindow.setLayoutData(gd_chatWindow);

		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
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
		GridData gd_btnSend = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_btnSend.widthHint = 68;
		gd_btnSend.heightHint = 62;
		btnSend.setLayoutData(gd_btnSend);
		btnSend.setText("Send");

		usersList = new List(composite, SWT.BORDER);
		GridData gd_usersList = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_usersList.widthHint = 148;
		usersList.setLayoutData(gd_usersList);

		usersUpdater = new UsersUpdater();
		usersUpdater.start();
		chatUpdater = new ChatUpdater();
		chatUpdater.start();

		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				try {
					usersUpdater.end();
					chatUpdater.end();

					chatUpdater.join();
					usersUpdater.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	public void updateTab() {
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
			if (connection != null && connection.connected) {
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
		private final long MSG_UPDATE_INTERVAL = 1000;


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
			System.out.println("Updating chat");
			
			if (connection != null && connection.connected) {
				try {
					synchronized (connection) {
						try {
							msg = connection.recv();
						} catch (SocketTimeoutException e) {
							System.err.println("timed out");
						}
					}
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
								usersList.removeAll();
								for (String u : sorted)
									usersList.add(u);
							}
						});
					} else if (r.isALL_MSG()) {
						String data = r.getData();
						int c = 0;
						while (data.charAt(c++) != ' ')
							;
						String from = data.substring(0, c - 1);
						String text = data.substring(c);
						final String line = from + ": " + text;
						
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (!"".equals(chatWindow.getText()))
									chatWindow.setText(chatWindow.getText() + "\n" + line);
								else
									chatWindow.setText(line);
							}
						});
						
					} else if (r.isPRIV_MSG()) {
						String data = r.getData();
						int c = 0;
						while (data.charAt(c++) != ' ')
							;
						String from = data.substring(0, c - 1);
						String text = data.substring(c);
						final String line = from + ": " + text;
						
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (!"".equals(chatWindow.getText()))
									chatWindow.setText(chatWindow.getText() + "\n" + line);
								else
									chatWindow.setText(line);
							}
						});
						
					} else {
						// TODO some error, response of no type
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
