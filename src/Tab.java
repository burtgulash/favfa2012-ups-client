import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;


public class Tab extends Composite {
	private Text text;
	private UsersUpdater usersUpdater;
	private List usersList;
	public Connection connection;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Tab(Composite parent, int style) {
		super(parent, style);
		setLayout(new RowLayout(SWT.VERTICAL));
		
		CLabel lblConnectionInfo = new CLabel(this, SWT.NONE);
		lblConnectionInfo.setAlignment(SWT.CENTER);
		lblConnectionInfo.setLayoutData(new RowData(445, SWT.DEFAULT));
		lblConnectionInfo.setText("Connection Info");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new RowData(435, 253));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new RowLayout(SWT.VERTICAL));
		composite_1.setLayoutData(new RowData(266, 234));
		
		StyledText styledText = new StyledText(composite_1, SWT.BORDER);
		styledText.setLayoutData(new RowData(244, 132));
		
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite_2.setLayoutData(new RowData(251, 86));
		
		text = new Text(composite_2, SWT.BORDER);
		text.setLayoutData(new RowData(143, 68));
		
		Button btnSend = new Button(composite_2, SWT.NONE);
		btnSend.setLayoutData(new RowData(80, 79));
		btnSend.setText("Send");
		
		usersList = new List(composite, SWT.BORDER);
		usersList.setLayoutData(new RowData(159, 226));
		
		usersUpdater = new UsersUpdater();
		
		
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				try {
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
	
	private class UsersUpdater extends Thread {
		public void run() {
			while (true) {
				String users = null;
				try {
					users = connection.r.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				ArrayList<String> sorted = new ArrayList<String>();
				StringTokenizer izer = new StringTokenizer(users);
				
				while (izer.hasMoreTokens())
					sorted.add(izer.nextToken());
				
				Collections.sort(sorted);
				
				synchronized(usersList) {
					for (String u : sorted)
						usersList.add(u);
				}
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class ChatListener extends Thread {
		public void run() {
			
		}
	}
}
