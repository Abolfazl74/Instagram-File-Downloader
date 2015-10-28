
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Abolfazl74
 */
public class Main extends javax.swing.JFrame {
    
    boolean status = false; // [درحال اجرا/متوقف شده] بررسی وضعیت برنامه
    boolean engineStat = false; // بررسی صحت آدرس
    String imgURLContent; // محتویات باکس ورودی کاربر
    String img; // محتویات باکس ورودی کاربر [پردازش شده]
    URL url; // [کاربر] آدرس دانلود
    URL dlURL; // آدرس دانلود فایل
    InputStream is = null;
    BufferedReader reader;
    String line;
    int imgLineBlock = 1;
    String hostname = "Unknown"; // نام سیستم کاربر
    String opUName = System.getProperty("user.name"); // گرفتن نام کاربری سیستم عامل
    String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    String sizeRegex = "[a-z]{1}[0-9]{3}x[0-9]{3}";
    String tempname;
    String pType;
    String fileFormat;
    String size = null;
    private SecureRandom random = new SecureRandom();
    Pattern r; // الگو برای ریجکس
    Font contentF = new Font("Serif", Font.BOLD, 20);
    Font dlResponseF = new Font("Serif", Font.BOLD, 22);
    ImageIcon favIcon = new ImageIcon(getClass().getResource("/Images/Original.png"));
    ImageIcon appIcon = new ImageIcon(getClass().getResource("/Images/AboutPage_MyTimer.png"));
    JFrame dlFrame;
    
    public Main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        
        Color c = new Color(216, 216, 216);
        Container con = getContentPane();
        con.setBackground(c);
        setIconImage(favIcon.getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        imgURL.requestFocus(); // فوکوش خودکار بر روی فیلد ورودی
        setResizable(false); // غیر قابل تغییر کردن سایز فریم
        setTitle("InstaImage Downloader [Coded BY Abolfazl74]"); // نام نرم افزار در نوار وضعیت
        try { // بدست آوردن نام سیستم کاربر
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException ex) {
            System.out.println("Hostname can not be resolved");
        }
        Welcome.setText("سلام   " + hostname + ") " + opUName + ") ");
        image.setSelected(true);
        //video.setEnabled(false);

    }
    
    private String getContent(String insURL, String type) {
        status = true;
        switch (type) {
            case "image":
                type = "<meta property=\"og:image\"";
                break;
            case "video":
                type = "<meta property=\"og:video\"";
                break;
            default:
                type = "<meta property=\"og:image\"";
        }
        try {
            url = new URL(insURL);
            is = url.openStream();  // throws an IOException
            reader = new BufferedReader(new InputStreamReader(is));
            
            while ((line = reader.readLine()) != null) {
                if (line.contains(type)) {
                    engineStat = true;
                    return line;
                } else if (line.contains("<body class=\" p-error dialog-404\">") || line.contains("<body class=\"p-error dialog-404\">")) {
                    engineStat = false;
                    return null;
                }
            }
        } catch (ConnectException e) {
            engineStat = false;
            JOptionPane.showMessageDialog(null, "<html><h3>" + "متاسفانه مشکلی در ارتباط با شبکه به وجود امده است\n" + "<html><h1>لطفااتصالات خود را بررسی نمایید", "Error !! Connection Refused", JOptionPane.ERROR_MESSAGE);
        } catch (MalformedURLException mue) {
            engineStat = false;
            JOptionPane.showMessageDialog(null, "آدرس وارد شده اشتباه است !!", "Error !! 500 Bad GateWay", JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException fne) {
            engineStat = false;
            JOptionPane.showMessageDialog(null, "متاسفانه عکسی در آدرس مورد نظر وجود ندارد", "Error !! 404 Not Found", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            engineStat = false;
            JOptionPane.showMessageDialog(null, "متاسفانه مشکلی در اجرای عملیات به وجود آمد ...", "Error !! On Compiling application", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                StartBtn.setEnabled(true);
                status = false;
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        return null;
    }
    
    private String sepUrl(String img, String type) {
        
        switch (type) {
            case "image":
                if (img.contains("property=\"og:image\"")) {
                    return img.substring(img.indexOf("content=\"") + 9, img.lastIndexOf("\""));
                } else {
                    return null;
                }
            case "video":
                if (img.contains("property=\"og:video\"")) {
                    return img.substring(img.indexOf("content=\"") + 9, img.lastIndexOf("\""));
                } else {
                    return null;
                }
            default:
                if (img.contains("property=\"og:image\"")) {
                    return img.substring(img.indexOf("content=\"") + 9, img.lastIndexOf("\""));
                } else {
                    return null;
                }
        }
        
    }
    
    public boolean imgUrlCheck(String img) {
        r = Pattern.compile(regex);
        Matcher m = r.matcher(img);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }
    
    public void imageSizeUrl(String img) {
        r = Pattern.compile(sizeRegex);
        Matcher m = r.matcher(img);
        if (m.find()) {
            this.size = m.group(0);
            this.img = img.replace(m.group(0) + "/", "");
        } else {
            size = null;
        }
    }
    
    public void saveFile(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
        long completeFileSize = httpConnection.getContentLength();
        
        try (java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream())) {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(destinationFile);
            java.io.BufferedOutputStream bout = new BufferedOutputStream(
                    fos, 2048);
            byte[] data = new byte[2048];
            long downloadedFileSize = 0;
            int x = 0;
            while ((x = in.read(data, 0, 2048)) >= 0) {
                downloadedFileSize += x;

                // calculate progress
                final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 1000d);

                // download progress bar
                //respondLabel.setText(String.valueOf(currentProgress));
                //System.out.println(currentProgress);
                //dlPer.setText(String.valueOf(currentProgress));
                NumberFormat defaultFormat = NumberFormat.getPercentInstance();
                defaultFormat.setMinimumFractionDigits(0);
                dlFrame.setTitle(defaultFormat.format(currentProgress) + "<--××در حال دانلود××");
                
                bout.write(data, 0, x);
            }
            bout.close();
        } catch (ConnectException e) {
            // Test
        }
        
        StartBtn.setEnabled(true);
        status = false;
    }
    
    public void openFileinBrowser(String url) throws IOException {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (URISyntaxException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><h3>متاسفانه سیستم شما از باز کردن مرورگر توسط ما جلوگیری میکند");
            }
        }
    }
    
    public void popUpMessage() {
        
        dlFrame = new JFrame("دانلود " + pType); // ساخت یک فریم جدید
        dlFrame.setType(javax.swing.JFrame.Type.UTILITY);
        JPanel dlPanel = new JPanel(); // ساخت پنل برای فریم
        dlFrame.setSize(600, 200); // ست کردن سایز فریم
        dlFrame.setResizable(false); // غیر قابل تغییر کردن سایز فریم
        dlFrame.add(dlPanel, BorderLayout.CENTER); // ادد کردن پنل در فریم
        dlFrame.setLocationRelativeTo(null); // Set winddows form location relative to null .
        dlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Set defualt exit task for frame.
        dlFrame.setVisible(true); // Set visible windows form .
        JTextPane contentL = new JTextPane();
        contentL.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        if (size != null) {
            contentL.setFont(contentF);
            contentL.setContentType("text/html");
            contentL.setText("<html><font color='blue'>" + pType + " مورد نظر یافت شد " + "\n<br /> سایز فایل شما در اینستاگرام فقط به صورت " + size + "\n<br />قابل مشاهده بود  ، اما ما برای شما فایل را به بزرگترین سایز تبدیل کردیم " + "\n<br />در حال دانلود فایل  ... ");
        } else {
            contentL.setFont(contentF);
            contentL.setContentType("text/html");
            contentL.setText("<html><font color='blue'>" + pType + " مورد نظر یافت شد \nدر حال دانلود فایل  ...");
        }
        JLabel dlResponse = new JLabel();
        dlPanel.add(contentL, BorderLayout.EAST);
        dlResponse.setFont(dlResponseF);
        dlPanel.add(dlResponse, BorderLayout.CENTER);
        dlPanel.revalidate();
        dlPanel.repaint();
        validate();
        
        if (engineStat && img != null && imgUrlCheck(img)) {
            
            try {
                saveFile(img, tempname = img.substring(img.lastIndexOf(".") - 8, img.lastIndexOf(".") - 1) + randomInt() + fileFormat);
                dlResponse.setText("<html><font color='green' size='20px'> فایل در  " + "<font color='red'>" + System.getProperty("user.dir") + "\\" + "<font color='green'> با موفقیت ذخیره شد<br />");
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "متاسفانه در ذخیره سازی تصویر با مشکلی رو به رو شدیم .. \nاما ما برای شما تصویر را در مرورگرتان باز میکنیم ... :)", "Access denied on making directoty", JOptionPane.ERROR_MESSAGE);
                try {
                    openFileinBrowser(img);
                } catch (IOException ex1) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }
    
    public String randomInt() {
        return new BigInteger(130, random).toString(12);
    }

    private void StartBtnActionPerformed(java.awt.event.ActionEvent evt) {                                         
        
        if (status == false) { // اگر عمیلات شروع نشده بود

            if (image.isSelected()) {
                pType = "image";
            } else {
                pType = "video";
            }
            
            if (pType.equals("image")) {
                fileFormat = ".jpg";
            } else {
                fileFormat = ".mp4";
            }
            
            StartBtn.setEnabled(false);
            imgURLContent = imgURL.getText();
            if (imgURLContent.length() > 10 || (imgURLContent.equals("") == false)) {
                try {
                    img = getContent(imgURLContent, pType);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error");
                }
                
                if (engineStat && img != null) {
                    img = sepUrl(img, pType);
                    if (pType.equals("image")) {
                        imageSizeUrl(img);
                    }
                    if (size != null) {
                        respondLabel.setText("<html><font color='green'>فایل مورد نظر شما در بزرگترین سایز دانلود خواهد شد");
                    } else {
                        respondLabel.setText("<html><font color='green'>فایل مورد نظر شما در سایز اصلی دانلود خواهد شد");
                    }
                } else {
                    respondLabel.setText(" آدرس وارد شده معتبر نیست  :'(");
                    StartBtn.setEnabled(true);
                }

                // popup Frame
                if (engineStat) {
                    popUpMessage();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "لطفا منتظر بمانید تا برنامه دستور قبلی شما را انجام بدهد ...", "هشدار !!!", JOptionPane.INFORMATION_MESSAGE);
        }

    }                                        

    private void AboutActionPerformed(java.awt.event.ActionEvent evt) {                                      
        
        JFrame AboutF = new JFrame("توضیحات"); // ساخت یک فریم جدید
        AboutF.setType(javax.swing.JFrame.Type.UTILITY);
        JPanel AboutP = new JPanel(); // ساخت پنل برای فریم
        AboutF.setSize(800, 700); // ست کردن سایز فریم
        AboutF.setResizable(false); // غیر قابل تغییر کردن سایز فریم
        AboutF.add(AboutP, BorderLayout.CENTER); // ادد کردن پنل در فریم
        AboutF.setLocationRelativeTo(null); // Set winddows form location relative to null .
        AboutF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Set defualt exit task for frame.
        AboutF.setVisible(true); // Set visible windows form .
        JLabel AboutL = new JLabel("", appIcon, JLabel.CENTER);
        AboutP.add(AboutL, BorderLayout.SOUTH);
        JLabel taskHelp = new JLabel();
        AboutP.add(taskHelp);
        taskHelp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    }                                     

    private void videoActionPerformed(java.awt.event.ActionEvent evt) {                                      
        JOptionPane.showMessageDialog(null, "درود !! \nمتاسفانه در این ورژن برنامه سرعت دانلود فیلم ها کمی پایین است  :(\nدر ورژن های بعدی قطعا این مشکل رفع خواهد شد .", "هشدار !!", JOptionPane.INFORMATION_MESSAGE);
    }                                   

    // Variables declaration - do not modify                     
    private javax.swing.JButton About;
    private javax.swing.JLabel Detail1;
    private javax.swing.JLabel PMail;
    private javax.swing.JToggleButton StartBtn;
    private javax.swing.JLabel Welcome;
    private javax.swing.ButtonGroup downloadType;
    private javax.swing.JRadioButton image;
    private javax.swing.JTextField imgURL;
    private javax.swing.JLabel respondLabel;
    private javax.swing.JRadioButton video;
    // End of variables declaration                   
}
