import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import java.net.*;
import java.io.*;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.SwingConstants;



public class FTP_Client {

	private JFrame frame;
	
	//Socket ready
	Socket ctrlSocket;	//Used to control socket
	public PrintWriter ctrlOutput;	//Control the data stream in output
	public BufferedReader ctrlInput;	//Control the data stream in input
		
	final int CTRLPORT=21;	//the port number of ftp in control
	private JTextField textFieldH;
	private JTextField textFieldUN;
	private JTextField textFieldPW;
	private JTextField textField;
	private JTextArea textArea;
	private JButton loginBtn;
	

	private JButton cdBtn;
	private JButton lsBtn;
	private JButton nlstBtn;
	private JButton getBtn;
	private JButton putBtn;
	private JButton deleteBtn;
	private JButton mkdBtn;
	private JButton rmdBtn;
	private JButton pwdBtn;
	private JButton typeiBtn;
	private JButton typeaBtn;
	
	private FTP_Client window;
	private boolean isLogon=false;
	private String command="";
	private JTextArea textArea_1;
	private JLabel lblNewLabel_1;
	
	//System.property("java.class.path")
	
	//Used to control Data stream that consists of address and port number
		public void openConnection(String host)
		throws IOException,UnknownHostException
		{
			ctrlSocket=new Socket(host,CTRLPORT);
			ctrlOutput=new PrintWriter(ctrlSocket.getOutputStream());
			ctrlInput=new BufferedReader(new InputStreamReader(ctrlSocket.getInputStream()));
		}
		
		//close the socket that used to control
		public void closeConnection()
		throws IOException
		{
			ctrlSocket.close();
		}
		
		public void showMenu()
		{
			System.out.println(">Command?");
			System.out.print("1 login ");
			System.out.print("2 ls ");
			System.out.print("3 cd ");
			System.out.print("4 get ");
			System.out.print("5 put ");
			System.out.print("6 ascii ");
			System.out.print("7 binary ");
			System.out.println("9 quit");
		}
		
		public String getCommand()
		{
			String buf="";
			BufferedReader lineread=new BufferedReader(new InputStreamReader(System.in));
			while(buf.length()!=1)
			{
				try {
					buf=lineread.readLine();
				}catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
			return (buf);
		}
		
		public void doLogin()
		{
			String loginName="";
			String password="";
			
			try {
				
				System.out.println("請輸入使用者名稱:");
				loginName=textFieldUN.getText();
				ctrlOutput.println("USER "+loginName);
				ctrlOutput.flush();
				System.out.println("請輸入密碼:");
				password=textFieldPW.getText();
				ctrlOutput.println("PASS "+password);
				ctrlOutput.flush();
				
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doCd()
		{
			String dirName="";
			//BufferedReader lineread=new BufferedReader(new InputStreamReader(System.in));
			
			try {
				System.out.println("請輸入目錄名稱:");
				
				//dirName=lineread.readLine();
				dirName=textField.getText();
				ctrlOutput.println("CWD "+dirName);//CWD command
				ctrlOutput.flush();
				textField.setText("");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doLs()
		{
			try {
				int n;
				byte[] buff=new byte[1024];
				
				//Build up the connection of data flow
				Socket dataSocket=dataConnection("LIST ");
				
				//Ready to read the data flow in data used
				BufferedInputStream dataInput=new BufferedInputStream(dataSocket.getInputStream());
				
				//Read the messages from list
				while((n=dataInput.read(buff))>0)
				{
					String temp=new String(buff);
					textArea.append(temp);
					System.out.write(buff,0,n);
				}
				textArea.append("\n");
				dataSocket.close();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doLs2()
		{
			try {
				textArea_1.setText("");
				int n;
				byte[] buff=new byte[1024];
				
				//Build up the connection of data flow
				Socket dataSocket=dataConnection("LIST ");
				
				//Ready to read the data flow in data used
				BufferedInputStream dataInput=new BufferedInputStream(dataSocket.getInputStream());
				
				//Read the messages from list
				while((n=dataInput.read(buff))>0)
				{
					String temp=new String(buff);
					textArea_1.append(temp);
					System.out.write(buff,0,n);
				}
				textArea_1.append("\n");
				dataSocket.close();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		
		public void doAscii()
		{
			try {
				ctrlOutput.println("TYPE A");//mode A
				ctrlOutput.flush();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doBinary()
		{
			try {
				ctrlOutput.println("TYPE I");//I mode
				ctrlOutput.flush();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public File doPickfile()
		 {
			 JFileChooser fileChooser=new JFileChooser();
			 fileChooser.setDialogTitle("Upload the file - select");
			 if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
			 {
				 //Get the file
				 java.io.File file=fileChooser.getSelectedFile();
				 return file;
			 }
			 return null;
				 
		 }
		
		
		public File doPickServer()
		 {
			 //String path=System.getProperty("java.class.path");
			 JFileChooser fileChooser=new JFileChooser();
			 fileChooser.setDialogTitle("Download the file - select");
			 if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
			 {
				 //Get the file
				 java.io.File file=fileChooser.getSelectedFile();
				 return file;
			 }
			 
			 return null;
				 
		 }
		
		
		public File doPickDir()//When user select the directory which he/she would like to store the file
		{
			JFileChooser fileChooser=new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle("Download the file - Directory select");
			if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
			{
				java.io.File file=fileChooser.getSelectedFile();
				return file;
			}
			return null;
		}
		
		
		public void doGet()//download the file from host
		{
			
			String path=System.getProperty("java.class.path");
			String filename=textField.getText();
			textField.setText("");	

			String[] tokens = filename.split("\\.");
			if(!filename.contains(".")||tokens.length!=2)
			{
				textArea.append("Wrong entry!! Please check again!\n");
			}

			else
			{
				try {
					int n;
					byte[] buff=new byte[1024];
					FileOutputStream outfile=null;

					//design the file name in the server
					System.out.println("請輸入檔案名稱:");
						
					//The file is ready to be the use of receive in client
					outfile=new FileOutputStream(filename);
		
					//fileName=lineread.readLine();
					File te=new File(filename);
					//Construct a data flow that is used to transfer files
					try {
						if(te.exists())
						{
							Socket dataSocket=dataConnection("RETR "+filename);
							BufferedInputStream dataInput=new BufferedInputStream(dataSocket.getInputStream());
							//Write down the context to client file,if there is data received from server
							while((n=dataInput.read(buff))>0)
							{
								outfile.write(buff,0,n);
							}
							
							dataSocket.close();
							outfile.close();
						}
					}catch(Exception e)
					{
						e.printStackTrace();
						System.exit(1);
					}
								
					textArea.append("Please select the file directory which you would like to place.\n");
					File pickedDir=doPickDir();//Selected by using JfileChooser

					//InputStream inStream=null;
					//OutputStream outStream=null;
					File temp=new File(path);		//Move the target file to the destination directory
					Path aFile=Paths.get(temp.getParent()+"\\"+filename);
					Path bFile=Paths.get(pickedDir.getPath()+"\\"+filename);
					File b=new File(pickedDir.getPath()+"\\"+filename);
					System.out.println(aFile);
					System.out.println(bFile);
					//Path a=aFile.toPath();
					//Path b=bFile.toPath();
					//System.out.println(a);
					//System.out.println(b);
					

						if(!b.exists()||b.getPath().equals(bFile.toString()))	//If there is not the same file name have been placed,move it to the directory
						{														//Or the file you would like to download now is more recent version,replace the original one
							Files.move(aFile,bFile);
							textArea.append("File '"+filename+"'has been downloaded to the path :\n");
							textArea.append(pickedDir+"\n");
						}
						
						else
						{
							textArea.append("Directory is wrong or file has exists!!\n\n");
							System.out.println("檔案不存在");
						}
						

						/*inStream=new FileInputStream(aFile);
						outStream=new FileOutputStream(bFile);
						
						int length;
						while((length=dataInput.read(buff))>0)
						{
							outStream.write(buff, 0, length);
						}*/
						
						/*inStream.close();
						outStream.close();
						aFile.delete();*/

					
				}catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
			//textArea.append("Please select the file which you would like to download.\n");
			
		}
		
		
		public void doPut()//Upload the file to the host
		{
			textArea.append("Please select the file which you would like to upload.\n");
			try {
				int n;
				byte[] buff=new byte[1024];
				FileInputStream sendfile=null;
				File picked=doPickfile();
				
				//Assign the file name
				System.out.println("請輸入檔案名稱:");
				//fileName=lineread.readLine();
				
				try {
					sendfile=new FileInputStream(picked);
				}catch(Exception e)
				{
					textArea.append("Not selected the file or the file doesn't exist!!\n\n");
					System.out.println("檔案不存在");
					return;
				}
				//Ready to read the file from client
				//FileInputStream sendfile=new FileInputStream(fileName);
				//Ready the data flow that is used to transfer
				Socket dataSocket=dataConnection("STOR "+picked.getName());
				OutputStream outstr=dataSocket.getOutputStream();
				//Read the file and deliver to server by making use of Internet
				while((n=sendfile.read(buff))>0)
				{
					outstr.write(buff,0,n);
				}
				textArea.append("File name '"+picked.getName()+"' has been uploaded to the remote end.\n");
				dataSocket.close();
				sendfile.close();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doNlst()
		{

			try {
				int n;
				byte[] buff=new byte[1024];
				
				//Build up the connection of data flow
				Socket dataSocket=dataConnection("NLST ");
				
				//Ready to read the data flow in data used
				BufferedInputStream dataInput=new BufferedInputStream(dataSocket.getInputStream());
				
				//Read the messages from list
				while((n=dataInput.read(buff))>0)
				{
					String temp=new String(buff);					
					textArea.append(temp);
					System.out.write(buff,0,n);
				}
				textArea.append("\n");
				dataSocket.close();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doDele()
		{
			String fileName="";
			
			try {
				System.out.println("請輸入檔案名稱:");
				
				fileName=textField.getText();
				ctrlOutput.println("DELE "+fileName);//RMD command
				ctrlOutput.flush();
				textField.setText("");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doRmd()
		{
			String dirName="";
			
			try {
				System.out.println("請輸入目錄名稱:");
				
				dirName=textField.getText();
				ctrlOutput.println("RMD "+dirName);//RMD command
				ctrlOutput.flush();
				textField.setText("");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public void doMkd()
		{
			String dirName="";
			
			try {
				System.out.println("請輸入目錄名稱:");
				
				dirName=textField.getText();
				ctrlOutput.println("MKD "+dirName);//MKD command
				ctrlOutput.flush();
				textField.setText("");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		
		public void doPwd()//PWD command
		{
			try {
				ctrlOutput.println("PWD");
				ctrlOutput.flush();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		

		public void doQuit()//QUIT command
		{
			try {
				ctrlOutput.println("QUIT");
				ctrlOutput.flush();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		public Socket dataConnection(String ctrlcmd)
		{
			
			String cmd="PORT ";	//Store the transfered data in PORT command
			//int i;
			Socket dataSocket=null;	//The socket used to send data
			try {
				//Get the own address
				byte[] address=InetAddress.getLocalHost().getAddress();
				//Construct a server in an appropriate port number
				ServerSocket serverDataSocket=new ServerSocket(0,1);
				//Ready to send data flow that transfer PORT command
				/*for(i=0;i<4;++i)
					cmd=cmd+(address[i]&0xff)+".";*/
				cmd=cmd+"127,0,0,1,";
				cmd=cmd+(((serverDataSocket.getLocalPort())/256)&0xff)
						+","
						+(serverDataSocket.getLocalPort()&0xff);
				//Make use of data flow in control to send PORT command
				ctrlOutput.println(cmd);
				ctrlOutput.flush();
				//Send operation command (LIST,PETR,STOR) to server
				ctrlOutput.println(ctrlcmd);
				ctrlOutput.flush();
				//Accept the connection of server
				dataSocket=serverDataSocket.accept();
				serverDataSocket.close();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			return dataSocket;
		}
		
		

		public boolean execCommand(String command)
		{
			boolean cont=true;
			switch(Integer.parseInt(command))
			{
			case 1:
				doLogin();
				break;
			case 2:
				doLs();
				break;
			case 3:
				doCd();
				break;
			case 4:
				doGet();
				break;
			case 5:
				doPut();
				break;
			case 6:
				doAscii();
				break;
			case 7:
				doBinary();
				break;
			case 9:
				doQuit();
				cont=false;
				break;
			default:
				System.out.println("請選擇一個選項:");
			}
			return(cont);
		}
		
		public void main_proc()
		throws IOException
		{
			boolean cont=true;
			try {
				while(cont) {
					showMenu();
					cont=execCommand(getCommand());
				}
			}
			catch(Exception e)
			{
				System.err.print(e);
				System.exit(1);
			}
		}
		
		
		public void getMsgs()
		{
			try {
				CtrlListen listener=new CtrlListen(ctrlInput);
				Thread listenerthread=new Thread(listener);
				listenerthread.start();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
		 class CtrlListen implements Runnable{
			BufferedReader ctrlInput=null;
			
			public CtrlListen(BufferedReader in)
			{
				ctrlInput=in;
			}
			
			public void run()
			{
				while(true)
				{
					try {
						String message=ctrlInput.readLine();
						String[] tokens = message.split("\\ ");
						System.out.println(message);
						
						if(tokens[0].equals("230")) //Log on successfully
						{
							isLogon=true;
							textArea.append(message+"\n");
							textFieldUN.setEditable(false);		//Prevent from editing text 
							textFieldPW.setEditable(false);
							loginBtn.setEnabled(false);
							
							cdBtn.setEnabled(true);		//Command function Button open
							lsBtn.setEnabled(true);
							nlstBtn.setEnabled(true);
							getBtn.setEnabled(true);
							putBtn.setEnabled(true);
							deleteBtn.setEnabled(true);
							mkdBtn.setEnabled(true);
							rmdBtn.setEnabled(true);
							pwdBtn.setEnabled(true);
							typeiBtn.setEnabled(true);
							typeaBtn.setEnabled(true);
							
							
							frame.setTitle("FTP Client - "+textFieldUN.getText());
							doLs2();
						}
						
						else if(tokens[0].equals("250")) // Directory deleted successfully,change directory
						{
							textArea.append(message+"\n");
							doLs2();
							textField.setEditable(false);
						}
						
						else if(tokens[0].equals("257")) // New directory created successfully
						{
							textArea.append(message+"\n");
							doLs2();
							textField.setEditable(false);
						}
						
						else if(tokens[0].equals("550")) //CWD failed. directory not found.
						{
							textArea.append(message+"\n");
							doLs2();
							textField.setEditable(false);
						}
						//message.equals("200 Type set to A")||message.equals("200 Type set to I")
						else if(tokens[0].equals("200")&&tokens[1].equals("Type")) //Type mode transform between ASCII and Binary
						{
							textArea.append(message+"\n");
						}
						
						else if(tokens[0].equals("200")&&command.equals("put"))//RETR command,upload successfully
						{
							textArea.append(message+"\n");
						}
						
						else if(tokens[0].equals("226")&&command.equals("put"))//RETR. download successfully
						{
							textArea.append(message+"\n");
						}
						
						else if(tokens[0].equals("220")) //Connect to the server successfully
						{
							textArea.append("Connect to the host successfully!"+"\n");
						}
						
						else if(tokens[0].equals("530"))
						{
							textArea.append(message+"\n");
						}
							
						//File information
						
						
					}catch(Exception e)
					{
						System.exit(1);
					}
				}
			}
		 }
		
		 
		 	 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FTP_Client window = new FTP_Client();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the application.
	 */
	public FTP_Client() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("FTP Client");
		
		JLabel labelH = new JLabel("Host");
		labelH.setFont(new Font("新細明體", Font.BOLD, 24));
		labelH.setBounds(10, 15, 46, 31);
		frame.getContentPane().add(labelH);
		
		textFieldH = new JTextField();
		textFieldH.setHorizontalAlignment(SwingConstants.LEFT);
		textFieldH.setEditable(false);
		textFieldH.setText("127.0.0.1");
		textFieldH.setFont(new Font("新細明體", Font.PLAIN, 16));
		textFieldH.setBounds(66, 21, 96, 21);
		frame.getContentPane().add(textFieldH);
		textFieldH.setColumns(10);
		
		JLabel labelUN = new JLabel("User Name");
		labelUN.setFont(new Font("新細明體", Font.BOLD, 20));
		labelUN.setBounds(367, 8, 96, 19);
		frame.getContentPane().add(labelUN);
		
		textFieldUN = new JTextField();
		textFieldUN.setFont(new Font("新細明體", Font.PLAIN, 16));
		textFieldUN.setBounds(464, 8, 115, 21);
		frame.getContentPane().add(textFieldUN);
		textFieldUN.setColumns(10);
		
		JLabel labelPW = new JLabel("Password");
		labelPW.setFont(new Font("新細明體", Font.BOLD, 20));
		labelPW.setBounds(377, 37, 106, 21);
		frame.getContentPane().add(labelPW);
		
		textFieldPW = new JTextField();
		textFieldPW.setFont(new Font("新細明體", Font.PLAIN, 16));
		textFieldPW.setBounds(464, 38, 115, 21);
		frame.getContentPane().add(textFieldPW);
		textFieldPW.setColumns(10);
		
		JButton connectBtn = new JButton("Connect");	//Build up the connection between client and host
		connectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					openConnection(textFieldH.getText());
					getMsgs();
					textFieldH.setEditable(false);
					connectBtn.setEnabled(false);
					loginBtn.setEnabled(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
					
			}
		});
		connectBtn.setFont(new Font("新細明體", Font.PLAIN, 20));
		connectBtn.setBounds(172, 15, 106, 33);
		frame.getContentPane().add(connectBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(204, 69, 475, 209);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		//textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		
		textField = new JTextField();
		textField.setFont(new Font("新細明體", Font.PLAIN, 14));
		textField.setEditable(false);
		textField.setBounds(204, 289, 375, 33);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton sendBtn = new JButton("Send");
		sendBtn.setEnabled(false);
		sendBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.append(textField.getText()+"\n\n");
				textField.setEditable(false);
				sendBtn.setEnabled(false);
				switch(command)//Base on the difference case,execute the corresponding function 
				{
				case "cd":
					doCd();
					break;
				case "mkd":
					doMkd();
					break;
				case "rmd":
					doRmd();
					break;
				case "dele":
					doDele();
					break;
				case "get":
					doGet();
					break;
				}
			}
		});
		sendBtn.setFont(new Font("新細明體", Font.PLAIN, 20));
		sendBtn.setBounds(592, 288, 87, 34);
		frame.getContentPane().add(sendBtn);
		
		loginBtn = new JButton("Log in");	//Execute the login function
		loginBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {	
					doLogin();
			}
		});
		loginBtn.setFont(new Font("新細明體", Font.PLAIN, 20));
		loginBtn.setBounds(592, 8, 87, 46);
		loginBtn.setEnabled(false);
		frame.getContentPane().add(loginBtn);
		
		JButton exitBtn = new JButton("QUIT");	
		exitBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(isLogon==false) //Determine whether the connection between host and client first
					System.exit(0);
				doQuit();			//If the connection has build,execute the quit function
			}
		});
		exitBtn.setFont(new Font("新細明體", Font.PLAIN, 20));
		exitBtn.setBounds(533, 563, 113, 41);
		frame.getContentPane().add(exitBtn);
		
		lsBtn = new JButton("LIST");	//List the current directory file in detail
		lsBtn.setEnabled(false);
		lsBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				command="ls";
				textArea.append("Switch to Command : LIST\n");
				doLs();
			}
		});
		lsBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		lsBtn.setBounds(10, 73, 87, 23);
		frame.getContentPane().add(lsBtn);
		
		cdBtn = new JButton("CWD");	//Change the directory
		cdBtn.setEnabled(false);
		cdBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				textField.setEditable(true);
				sendBtn.setEnabled(true);
				command="cd";
				textArea.append("Switch to command : CWD\n");
				textArea.append("Please type the name of directory that you would like to switch to : ");
			}
		});
		cdBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		cdBtn.setBounds(10, 172, 87, 23);
		frame.getContentPane().add(cdBtn);
		
		getBtn = new JButton("RETR");	//download the file from client to host
		getBtn.setEnabled(false);
		getBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				textField.setEditable(true);
				sendBtn.setEnabled(true);
				command="get";
				textArea.append("Switch to command : RETR\n");
				textArea.append("Please type the name of file(include Filename Extension)\nthat you would like to download : ");
			}
		});
		getBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		getBtn.setBounds(107, 106, 87, 23);
		frame.getContentPane().add(getBtn);
		
		putBtn = new JButton("STOR");	//upload the file from the host-remote end
		putBtn.setEnabled(false);
		putBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				command="put";
				textArea.append("Switch to command : STOR\n");
				doPut();
				doLs2();
			}
		});
		putBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		putBtn.setBounds(10, 106, 87, 23);
		frame.getContentPane().add(putBtn);
		
		deleteBtn = new JButton("DELE");	//delete the file(exclude the directory)
		deleteBtn.setEnabled(false);
		deleteBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				textField.setEditable(true);
				sendBtn.setEnabled(true);
				command="dele";
				textArea.append("Switch to command : DELE\n");
				textArea.append("Please type the name of file(include Filename Extension)\nthat you would like to delete : ");
			}
		});
		deleteBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		deleteBtn.setBounds(10, 205, 87, 23);
		frame.getContentPane().add(deleteBtn);
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File froot = null;
		for (File file : fileSystemView.getRoots())
		{
			froot=file;
		}
		
		/*JTree tree = new JTree();
		tree.setShowsRootHandles(true);
		scrollPane_1.setViewportView(tree);
		tree.setRootVisible(false);
		tree.setModel(new DefaultTreeModel(
				
			new DefaultMutableTreeNode() {
				{
					DefaultMutableTreeNode node_1;
					
					for (File file : fileSystemView.getRoots()) 
					{
						node_1 = new DefaultMutableTreeNode(file.getName());					
					    System.out.println("Root: " + file);
					    for (File f : file.listFiles()) 
					    {
					    	
					        if (f.isDirectory()) 
					        {
					        	node_1.add(new DefaultMutableTreeNode(f.getName()));
					            System.out.println("Child: " + f);
					        }
					        if(f.getParentFile().exists()&&f.isFile())
					        	textArea_2.append(f.getName());
					        else if(f.isFile())
					        {
					        	if(f.getParentFile().exists())
					        		f.getParentFile().add(new DefaultMutableTreeNode(f.getName()));
					        	node_1.add(new DefaultMutableTreeNode(f.getName()));
					        }
					        	
					    }
					    add(node_1);
					}
				}
			}
		));*/
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 362, 480, 242);
		frame.getContentPane().add(scrollPane_1);
		
		textArea_1 = new JTextArea();
		textArea_1.setFont(new Font("Monospaced", Font.PLAIN, 15));
		scrollPane_1.setViewportView(textArea_1);
		
		JLabel lblNewLabel = new JLabel("Remote End - Current Directory");
		lblNewLabel.setFont(new Font("新細明體", Font.BOLD, 20));
		lblNewLabel.setBounds(107, 331, 275, 21);
		frame.getContentPane().add(lblNewLabel);
		
		rmdBtn = new JButton("RMD");	//Remove the directory
		rmdBtn.setEnabled(false);
		rmdBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				textField.setEditable(true);
				sendBtn.setEnabled(true);
				command="rmd";
				textArea.append("Switch to command : RMD\n");
				textArea.append("Please type the name of directory that you would like to delete : ");
			}
		});
		rmdBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		rmdBtn.setBounds(107, 139, 87, 23);
		frame.getContentPane().add(rmdBtn);
		
		mkdBtn = new JButton("MKD");	//Make the directory
		mkdBtn.setEnabled(false);
		mkdBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				textField.setEditable(true);
				sendBtn.setEnabled(true);
				command="mkd";
				textArea.append("Switch to command : MKD\n");
				textArea.append("Please type the name of directory that you would like to create : ");
			}
		});
		mkdBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		mkdBtn.setBounds(10, 139, 87, 23);
		frame.getContentPane().add(mkdBtn);
		
		pwdBtn = new JButton("PWD");	//Print the current directory
		pwdBtn.setEnabled(false);
		pwdBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				command="pwd";
				textArea.append("Switch to command : PWD\n");
				doPwd();
			}
		});
		pwdBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		pwdBtn.setBounds(107, 172, 87, 23);
		frame.getContentPane().add(pwdBtn);
		
		nlstBtn = new JButton("NLST");	//Print whole file names in the directory
		nlstBtn.setEnabled(false);
		nlstBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				command="nlst";
				textArea.append("Switch to command : NLST\n");
				doNlst();
			}
		});
		nlstBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		nlstBtn.setBounds(107, 73, 87, 23);
		frame.getContentPane().add(nlstBtn);
		
		typeaBtn = new JButton("TYPE ASCII");	//Type mode switch to ASCII
		typeaBtn.setEnabled(false);
		typeaBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				command="typea";
				textArea.append("Switch to command : TYPE A\n");
				doAscii();
			}
		});
		typeaBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		typeaBtn.setBounds(10, 255, 129, 23);
		frame.getContentPane().add(typeaBtn);
		
		typeiBtn = new JButton("TYPE Binary");	//Type mode switch to Binary
		typeiBtn.setEnabled(false);
		typeiBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				command="typei";
				textArea.append("Switch to command : TYPE I\n");
				doBinary();
			}
		});
		typeiBtn.setFont(new Font("新細明體", Font.PLAIN, 16));
		typeiBtn.setBounds(10, 288, 129, 23);
		frame.getContentPane().add(typeiBtn);
		
		JTextArea txtrst = new JTextArea();
		txtrst.setEditable(false);
		txtrst.setFont(new Font("Monospaced", Font.BOLD, 14));
		txtrst.setLineWrap(true);
		txtrst.setBounds(502, 362, 177, 178);
		frame.getContentPane().add(txtrst);
		
		lblNewLabel_1 = new JLabel("User Tips");	//A small tips that offer to the user
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("新細明體", Font.BOLD, 18));
		lblNewLabel_1.setBounds(533, 332, 106, 29);
		frame.getContentPane().add(lblNewLabel_1);
		txtrst.append("\n");
		txtrst.append("1. Connect to the host first\n\n");
		txtrst.append("2. Type Name and password to log in\n\n");
		txtrst.append("3. Using the command function\n\n");
		
	}
}
