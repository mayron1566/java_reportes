/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporte;

import java.util.*;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable; 

import com.toedter.calendar.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author morellana03
 */
public class Reporte extends JFrame implements ActionListener {
    
    private String part[];
    private Date respaldo;
    private Date fechas;
    private String fechar;
    private JTable tabla;
    private JTable tabla2;
    private JTable tabla3;
    private JLabel texto;           // etiqueta o texto no editable
    private JTextField caja;        // caja de texto, para insertar datos
    private JButton boton;          // boton con una determinada accion
    private JDateChooser fecha;
    private String date;
    private Object[][] datos;
    private JScrollPane scroll;
    
    public Reporte() {
        super();                    // usamos el contructor de la clase padre JFrame
        configurarVentana();        // configuramos la ventana
        inicializarComponentes();   // inicializamos los atributos o componentes
    }
    
    
    //metodo que configura la ventana del programa
    private void configurarVentana() {
        this.setTitle("Reporte Accesos");                   // colocamos titulo a la ventana
        this.setSize(512, 210);                                 // colocamos tamanio a la ventana (ancho, alto)
        this.setLocationRelativeTo(null);                       // centramos la ventana en la pantalla
        this.setLayout(null);                                   // no usamos ningun layout, solo asi podremos dar posiciones a los componentes
        this.setResizable(false);                               // hacemos que la ventana no sea redimiensionable
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // hacemos que cuando se cierre la ventana termina todo proceso
    }
    
    
    private void inicializarComponentes() {
        // creamos los componentes
        texto = new JLabel();
        caja = new JTextField();
        boton = new JButton();
        fecha = new JDateChooser();
        fecha.setBounds(200, 50, 100, 25);
        // configuramos los componentes
        texto.setText("Generador de reportes de accesos");    // colocamos un texto a la etiqueta
        texto.setBounds(160, 20, 200, 80);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        //caja.setBounds(160, 50, 100, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        boton.setText("Generar Reporte");   // colocamos un texto al boton
        boton.setBounds(160, 100, 200, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton.addActionListener(this);      // hacemos que el boton tenga una accion y esa accion estara en esta clase
        // adicionamos los componentes a la ventana
        this.add(texto);
        //this.add(fecha);
        this.add(boton);
    }

    private void inicializarComponentes2() throws SQLException {
        DefaultTableModel modelo = new DefaultTableModel();
        DefaultTableModel model = new DefaultTableModel();
        Connection conn = null;
        tabla = new JTable(modelo);
        tabla2 = new JTable(model);
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
 
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File ("databases.txt");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
            // Lectura del fichero
            System.out.println("Leyendo el contendio del archivo.txt");
            String linea;
            while(br.ready()){
                linea = br.readLine();
                part = linea.split(",");
                for (int i=0; i<part.length; i++){
                    System.out.print(part[i] + " " );
                }
               System.out.println();             
                
            }
        }
        catch(Exception e){
           e.printStackTrace();
        }finally{
           // En el finally cerramos el fichero, para asegurarnos
           // que se cierra tanto si todo va bien como si salta 
           // una excepcion.
           try{
              if( null != fr ){
                 fr.close();
              }
           }catch (Exception e2){
              e2.printStackTrace();
           }
        }
        modelo.addColumn("Fecha");
        modelo.addColumn("Usuario afectado");
        modelo.addColumn("Accion");
        modelo.addColumn("Permiso afectado");
        modelo.addColumn("Usuario Autor");
        modelo.addColumn("Base de datos");
        modelo.addColumn("Nombre servidor");
        modelo.addColumn("Fecha de geneneracion del reporte");
        
        model.addColumn("Database Name");
        model.addColumn("User Name");
        model.addColumn("Role Name");
        model.addColumn("Nombre Servidor");
        model.addColumn("Fecha de consulta");
        
        for (int i=0; i<part.length; i++){
        String dbURL = "jdbc:sqlserver://"+part[i]+":1433;database=master;integratedSecurity=true";
        conn = DriverManager.getConnection(dbURL);
        Statement drop_function = conn.createStatement();
        drop_function.execute("IF Object_Id('dbo.SeeAccessControlChanges') IS NOT NULL\n" +
"     DROP function dbo.SeeAccessControlChanges;");
        Statement funcion = conn.createStatement();
        funcion.execute(" CREATE FUNCTION dbo.SeeAccessControlChanges\n" +
"  /**\n" +
"  Summary: >\n" +
"    This function gives you a list\n" +
"    of security events concerning users, roles and logins\n" +
"    from the default trace\n" +
"  Author: Phil Factor\n" +
"  Date: 04/10/2018\n" +
"  Examples:\n" +
"     - Select * from dbo.SeeAccessControlChanges(DateAdd(day,-1,SysDateTime()),SysDateTime())\n" +
"  columns: datetime_local, action, data, hostname, ApplicationName, LoginName, traceName, spid, EventClass, objectName, rolename, TargetLoginName, category_id, ObjectType \n" +
"  Returns: >\n" +
"        datetime_local datetime2(7)\n" +
"        action nvarchar(816)\n" +
"        data ntext\n" +
"        hostname nvarchar(256)\n" +
"        ApplicationName nvarchar(256)\n" +
"        LoginName nvarchar(256)\n" +
"        traceName nvarchar(128)\n" +
"        spid int\n" +
"        EventClass int\n" +
"        objectName nvarchar(256)\n" +
"        rolename nvarchar(256)\n" +
"        TargetLoginName nvarchar(256)\n" +
"        category_id smallint\n" +
"        ObjectType nvarchar(128)\n" +
"          **/\n" +
"    (\n" +
"    @Start DATETIME2,--the start of the period\n" +
"    @finish DATETIME2--the end of the period\n" +
"    )\n" +
"  RETURNS TABLE\n" +
"   --WITH ENCRYPTION|SCHEMABINDING, ..\n" +
"  AS\n" +
"  RETURN\n" +
"    (\n" +
"    SELECT \n" +
"        CONVERT(\n" +
"          DATETIME2,\n" +
"         SWITCHOFFSET(CONVERT(datetimeoffset, StartTime), DATENAME(TzOffset, SYSDATETIMEOFFSET()))\n" +
"               )  AS datetime_local, Coalesce( LoginName+ ' ','unknown ') AS usuario, \n" +
"        CASE EventSubclass --interpret the subclass for these traces\n" +
"          WHEN 1 THEN 'added ' WHEN 2 THEN 'dropped ' WHEN 3 THEN 'granted database access for ' \n" +
"          WHEN 4 THEN 'revoked database access from ' ELSE 'did something to ' END AS action, Coalesce(TargetLoginName,'') AS targetUser,\n" +
"        Coalesce( CASE EventSubclass WHEN 1 THEN ' to object ' ELSE ' from object ' end+objectName, '') AS donde,\n" +
"        Coalesce(TextData,'') AS [data], hostname, ApplicationName, LoginName, TE.name AS traceName, spid,\n" +
"        EventClass, objectName, rolename, TargetLoginName, TE.category_id, DT.DatabaseName,\n" +
"      SysTSV.subclass_name AS ObjectType\n" +
"       FROM::fn_trace_gettable(--just use the latest trace\n" +
"           (SELECT traces.path FROM sys.traces \n" +
"              WHERE traces.is_default = 1), DEFAULT) AS DT\n" +
"        LEFT OUTER JOIN sys.trace_events AS TE\n" +
"          ON DT.EventClass = TE.trace_event_id\n" +
"        LEFT OUTER JOIN sys.trace_subclass_values AS SysTSV\n" +
"          ON DT.EventClass = SysTSV.trace_event_id\n" +
"         AND DT.ObjectType = SysTSV.subclass_value\n" +
"      WHERE StartTime BETWEEN @start AND @finish\n" +
"      AND TargetLoginName IS NOT NULL\n" +
"    )");
        conn = DriverManager.getConnection(dbURL);
        Statement consulta2 = conn.createStatement();
        Statement call = conn.createStatement();
        Statement call2 = conn.createStatement();
        Statement call3 = conn.createStatement();
        call.execute("DECLARE @name sysname,\n" +
"@sql nvarchar(4000),\n" +
"@maxlen1 smallint,\n" +
"@maxlen2 smallint,\n" +
"@maxlen3 smallint\n" +
"CREATE TABLE #tmpTable\n" +
"(\n" +
"DBName sysname NOT NULL ,\n" +
"UserName sysname NOT NULL,\n" +
"RoleName sysname NOT NULL\n" +
")\n" +
"DECLARE c1 CURSOR for\n" +
"SELECT name FROM master.sys.databases WHERE state != 6\n" +
"OPEN c1\n" +
"FETCH c1 INTO @name\n" +
"WHILE @@FETCH_STATUS >= 0\n" +
"BEGIN\n" +
"SELECT @sql =\n" +
"'INSERT INTO #tmpTable\n" +
"SELECT N'''+ @name + ''', a.name, c.name\n" +
"FROM [' + @name + '].sys.database_principals a\n" +
"JOIN [' + @name + '].sys.database_role_members b ON b.member_principal_id = a.principal_id\n" +
"JOIN [' + @name + '].sys.database_principals c ON c.principal_id = b.role_principal_id\n" +
"WHERE a.name != ''dbo'''\n" +
"EXECUTE (@sql)\n" +
"FETCH c1 INTO @name\n" +
"END\n" +
"CLOSE c1\n" +
"DEALLOCATE c1\n" +
"SELECT @maxlen1 = (MAX(LEN(COALESCE(DBName, 'NULL'))) + 2)\n" +
"FROM #tmpTable\n" +
"SELECT @maxlen2 = (MAX(LEN(COALESCE(UserName, 'NULL'))) + 2)\n" +
"FROM #tmpTable\n" +
"SELECT @maxlen3 = (MAX(LEN(COALESCE(RoleName, 'NULL'))) + 2)\n" +
"FROM #tmpTable\n");
        ResultSet result2 = call2.executeQuery("SELECT LEFT(DBName, 50) AS Database_Name, LEFT(UserName, 50) AS Users_Name, LEFT(RoleName, 50) AS Roles_Name FROM #tmpTable ORDER BY UserName");
        call3.execute("DROP TABLE #tmpTable");
        ResultSet result = consulta2.executeQuery("use master; SELECT ADayBack.datetime_local, ADayBack.targetLoginName , ADayBack.action ,ADayBack.rolename, ADayBack.LoginName, ADayBack.DatabaseName FROM dbo.SeeAccessControlChanges (DateAdd(YEAR, -1, SysDateTime()), SysDateTime()) AS ADayBack ORDER BY datetime_local;");
        int j=0;
        System.out.println(result2);
        while(result.next())
        {
            Object [] fila = new Object[8]; // Hay 8 columnas en la tabla
            for (j=0;j<6;j++)
                fila[j] = result.getObject(j+1);
            fila[6]= part[i];
            fila[7]= fechar;
            modelo.addRow(fila);
            }
        while(result2.next())
        {
            Object [] fila2 = new Object[5]; // Hay 8 columnas en la tabla
            for (j=0;j<3;j++)
                fila2[j] = result2.getObject(j+1);
            fila2[3]= part[i];
            fila2[4]= fechar;
            model.addRow(fila2);
            }
        }
        
        // creamos los componentes
        // configuramos los componentes
    }
        
    @Override
    @SuppressWarnings("empty-statement")
    public void actionPerformed(ActionEvent e) {
        try {
            SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");
            this.fechar = formatter.format(new Date());
            inicializarComponentes2();    // mostramos un mensaje (frame, mensaje)
        } catch (SQLException ex) {
            Logger.getLogger(Reporte.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File archivoserver = null;
        FileReader arch = null;
        BufferedReader buffer = null;
        DefaultTableModel model2 = new DefaultTableModel();
        DefaultTableModel model3 = new DefaultTableModel();
        tabla3 = new JTable(model2);
        model2.addColumn("Nombre Servidor");
        model2.addColumn("Fecha");
        model2.addColumn("Se agrego usuario a grupo...");
        model2.addColumn("Usuario autor");
        model2.addColumn("Usuario afectado");
 
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivoserver = new File ("so.txt");
            arch = new FileReader (archivoserver);
            buffer = new BufferedReader(arch);
            // Lectura del fichero
            String linea;
            while(buffer.ready()){
                int aviso=0;
                linea = buffer.readLine();
                String parte[];
                parte = linea.split(",");
                Object [] fila3 = new Object[parte.length];
                for (int i=0; i<parte.length-1; i++){
                        if (i>=3){
                        fila3[i]=parte[i+1].replaceAll("[^\\dA-Za-z-]", "");
                        System.out.print(parte[i+1] + " " );
                        }
                        else{
                            fila3[i]=parte[i].replaceAll("[^\\dA-Za-z-]", "");
                            System.out.print(parte[i] + " " );
                        }
                    
                }
               model2.addRow(fila3);            
                
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Reporte.class.getName()).log(Level.SEVERE, null, ex);
        }        finally{
           // En el finally cerramos el fichero, para asegurarnos
           // que se cierra tanto si todo va bien como si salta 
           // una excepcion.
           try{
              if( null != arch ){
                 arch.close();
              }
           }catch (Exception e2){
              e2.printStackTrace();
           }
        }
        
        String desktopPath = System.getProperty("user.home") + "\\Desktop\\reporte";
        String desktopPath2 = System.getProperty("user.home") + "\\Desktop\\reporteUsuarios";
        String desktopPath3 = System.getProperty("user.home") + "\\Desktop\\reporteServidor";
        File archivo = new File(desktopPath+fechar+".xls");
        File archivo2 = new File(desktopPath2+fechar+".xls");
        File archivo3 = new File(desktopPath3+fechar+".xls");
        HSSFWorkbook libro = new HSSFWorkbook();
        HSSFWorkbook libro2 = new HSSFWorkbook();
        HSSFWorkbook libro3 = new HSSFWorkbook();
        HSSFSheet hoja = libro.createSheet();
        HSSFSheet hoja2 = libro2.createSheet();
        HSSFSheet hoja3 = libro3.createSheet();
        for (int i = 0; i < tabla.getRowCount()+1; i++) {
            HSSFRow fila = hoja.createRow(i);           
            if(i==0){
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    HSSFCell celda = fila.createCell(j);
                    celda.setCellValue(new HSSFRichTextString(tabla.getColumnModel().getColumn(j).getHeaderValue().toString()));
                }
            }else{
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    HSSFCell celda = fila.createCell(j);//vvvvhv
                    if(tabla.getValueAt(i-1, j)!=null)
                        celda.setCellValue(new HSSFRichTextString(tabla.getValueAt(i-1, j).toString()));
                }
            }//
            for (int k = 0; k < tabla.getColumnCount(); k++) {
                    hoja.autoSizeColumn(k);
                }
        }
        ////////
        for (int i = 0; i < tabla2.getRowCount()+1; i++) {
            HSSFRow fila2 = hoja2.createRow(i);           
            if(i==0){
                for (int j = 0; j < tabla2.getColumnCount(); j++) {
                    HSSFCell celda2 = fila2.createCell(j);
                    celda2.setCellValue(new HSSFRichTextString(tabla2.getColumnModel().getColumn(j).getHeaderValue().toString()));
                }
            }else{
                for (int j = 0; j < tabla2.getColumnCount(); j++) {
                    HSSFCell celda2 = fila2.createCell(j);//vvvvhv
                    if(tabla2.getValueAt(i-1, j)!=null)
                        celda2.setCellValue(new HSSFRichTextString(tabla2.getValueAt(i-1, j).toString()));
                }
            }
            for (int k = 0; k < tabla2.getColumnCount(); k++) {
                    hoja2.autoSizeColumn(k);
                }
        }
        ///////////////
        for (int i = 0; i < tabla3.getRowCount()+1; i++) {
            HSSFRow fila3 = hoja3.createRow(i);           
            if(i==0){
                for (int j = 0; j < tabla3.getColumnCount(); j++) {
                    HSSFCell celda3 = fila3.createCell(j);
                    celda3.setCellValue(new HSSFRichTextString(tabla3.getColumnModel().getColumn(j).getHeaderValue().toString()));
                }
            }else{
                for (int j = 0; j < tabla3.getColumnCount(); j++) {
                    HSSFCell celda3 = fila3.createCell(j);//vvvvhv
                    if(tabla3.getValueAt(i-1, j)!=null)
                        celda3.setCellValue(new HSSFRichTextString(tabla3.getValueAt(i-1, j).toString()));
                }
            }
            for (int k = 0; k < tabla3.getColumnCount(); k++) {
                    hoja3.autoSizeColumn(k);
                }
        }
        //////////////
        try {
                try (FileOutputStream elFichero = new FileOutputStream(archivo)) {
                    libro.write(elFichero);
                    elFichero.close();
                    JOptionPane.showMessageDialog(null, "Reporte de cambios de permisos de usuarios generado con exito");
                }
                try (FileOutputStream elFichero = new FileOutputStream(archivo2)) {
                    libro2.write(elFichero);
                    elFichero.close();
                    JOptionPane.showMessageDialog(null, "Listado total de usuarios en la base de datos generado con exito");
                }
                try (FileOutputStream elFichero = new FileOutputStream(archivo3)) {
                    libro3.write(elFichero);
                    elFichero.close();
                    JOptionPane.showMessageDialog(null, "Reporte de servidores generado con exito");
                }
            } catch (Exception eb) {
                JOptionPane.showMessageDialog(null, "Ocurrio un problema al generar uno o ambos reportes");
            }
        
    }
  
    //Conexion con sql server va aca
    public static void main(String[] args) {
        Reporte V = new Reporte();      // creamos una ventana
        V.setVisible(true);    // hacemos visible la ventana creada
    }
}
