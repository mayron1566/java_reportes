# java_reportes

Este programa fue creado para generar reportes de cambios en usuarios de las bases de datos que utilicen SQLServer y servidores con sistema operativo Windows Server.

El reporte que se genera entrega datos sobre el usuario afectado en un cambio de permisos, quien realizo el cambio, la fecha, en que servidor, en que base de datos, entre otros datos. Para esto se hace uso de un script de Powershell que extrae los datos del servidor y de un porgrama Java que extrae la informacion de las bases de datos ademas de juntar la informacion extraida con el script de Powershell para crear documentos Excel con todos los datos.

Requisitos del sistema:

  -Tener instalado JRE 8 o superior.
  
  -Tener instalado SQLServer o contar con acceso a este a travez de la red del hogar o trabajo.
  
NOTA: De no contar con la libreria sqljdbc_auth.dll, dentro de la carpeta dist se encuentra una carpeta llamada x86 y x64, dependiendo del sistema operativo copiar el archivo dentro de estas carpetas y copiarla en la carpeta Windows en la raiz del sistema
  
Instrucciones de uso:

  -Crear archivos databases.txt y servers.txt con los nombres de las bases de datos y de los servidores respectivamente.
  
  -Ejecutar Reporte_servers.bat para ejecutar el script de Powershell. Dependiendo del servidor puede tomar bastante tiempo en terminar
  
  -Una vez finalizado el paso anterior y generado el archivo so.txt ejecutar Reporte.jar.
  
  -Una vez abierto el programa presionar Generar reportes y esperar hasta que se de aviso de que los reportes fueron generados.
  
  -Cerrar el programa y revisar el escritorio donde apareceran los archivos.
  
  NOTA: Si ambos programas no registran cambios en permisos de usuario en los servidores o en las bases de datos los archivos saldran en   blanco. Esto es mas comun con los reportes de la base de datos ya que los registros no que se usan para verificar no son permanentes.
  
