package Clases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class ConsultasQuery {
    //*******************************************************************************************************
    //*****************************************Consulta para insertar registros******************************
    //*******************************************************************************************************
    public void ConsultaInsert(String consulta,File carpeta ){
        File tabla;
        //veremos si la tabla existe en los archivos
        tabla=tablaExist(carpeta, consulta);        
        //sino existe entonces se saldrá a mandar el error
        if(tabla==null)
            return;        
        
        
        //quitamos de la consulta la tabla que ya validamos si esta o no.
        consulta=consulta.replaceFirst(tabla.getName().replace(".txt","")+" ","");
        //si de casualidad no hay espacio entre el nombre de la tabla y los campos
        //de todos modos se quitará
        if(consulta.startsWith(tabla.getName().replace(".txt","")+"")){
            consulta=consulta.replaceFirst(tabla.getName().replace(".txt",""),"");
        }
        //vamos a separar los campos de los valores
        String[] campVal=consulta.split("values");
        String campos="";
        String valores="";
        try{
            campos=campVal[0];
            valores=campVal[1];
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Error de sintaxis 'values'","Erorr", ERROR_MESSAGE);
            return;
        }
        //*************** en esta parte nos deshacemos de todo el texto basura*******************************
        //vamos a quitarles los parentesis a los campos y los valores
        campos=campos.replace("(", "");
        campos=campos.replace(")", "");
        valores=valores.replace("(", "");
        valores=valores.replace(")", "");
        //quitaremos las comas simples
        valores=valores.replace("'", "");
        campos=campos.replace(" ", "");
        //**************************************************************************************************
        String[] camposTemp=campos.split(",");
        String[] valoresTemp=valores.split(",");
        int [] camp=seleccionarCamposArchivo(camposTemp,tabla);
        try{
            if(camp.length!=camposTemp.length){
                return;
            }
        }catch(Exception es){
            JOptionPane.showMessageDialog(null,"Los campos '"+campos+"' son incorrectos","ERROR",ERROR_MESSAGE);
            return;
        }
        
        if(camposTemp.length!=valoresTemp.length){
            JOptionPane.showMessageDialog(null,"Valores incompletos","ERROR",ERROR_MESSAGE);
            return;
        }
        
        if(Insertar(valores,tabla)){
            JOptionPane.showMessageDialog(null,"Registro exitoso");
        }
    }
    
    //*******************************************************************************************************
    //******hacemos los procedimientos y validaciones para hacer el delete en una tabla**********************
    //*******************************************************************************************************
    public void ConsultaDelete(String consulta,File carpeta){
        File tabla;
        //verificamos si la carpeta existe
        tabla=tablaExist(carpeta,consulta);
        //sino existe entonces se saldrá a mandar el error
        if(tabla==null)
            return;
        
        //quitamos de la consulta la tabla que ya validamos si esta o no.
        consulta=consulta.replaceFirst(tabla.getName().replace(".txt","")+" ","");
        
        //conseguimos en que posicion esta el registro con las condiciones
        int[] posicion=where(consulta,tabla);
        if(posicion==null)
            return;
        
        //vamos a leer los datos del archivo
        String [] datosArchivo=leerDatosArchivo(tabla);
        /* Se eliminan los registros que se encontraron.*/
        for(int i=0;i<posicion.length;i++){
            datosArchivo[posicion[i]]="";
        }
        //el registro que deseamos eliminar se quita del arreglo
        int contador=0;
        //vamos a hacer un arreglo con los mismos registros que estaban anteriormente
        //pero sin el registro que eliminamos recientemente
        String[] datos=new String[datosArchivo.length-posicion.length];
        for(int h=0;h<datosArchivo.length;h++){
            //para validar que no sea ingresado el registro que eliminamos
            if(!datosArchivo[h].isEmpty()){
                datos[contador]=datosArchivo[h];
                contador++;
            }
        }
                    //vamos a guardar los cambios en la carpeta
        EscribirArchivo(datos,tabla);
        JOptionPane.showMessageDialog(null,"Registro borrado");
    }
    
    
    //*******************************************************************************************************
    //******hacemos los procedimientos y validaciones para hacer el update en una tabla**********************
    //*******************************************************************************************************
    public void ConsultaUpdate(String consulta, File carpeta){
        File tabla;        
        //verificamos si la carpeta existe
        tabla=tablaExist(carpeta,consulta);
        //sino existe entonces se saldrá a mandar el error
        if(tabla==null)
            return;
        
        //quitamos de la consulta la tabla que ya validamos si esta o no.
        consulta=consulta.replaceFirst(tabla.getName().replace(".txt"," ")+"","");
        
        if(!consulta.startsWith("set ")){
            JOptionPane.showMessageDialog(null, "Error de sintaxis faltó 'set' en su consulta","Error", ERROR_MESSAGE);
            return;
        }
        
        //le quitamos el set a la consulta
        consulta=consulta.replaceFirst("set ", "");
        //vamos a dividir de columnas y valores en un string y en otro las condiciones del where
        // nombre='oscar', id=2 where id=1 and nombre='ismael'
        //ejemplo: nombre='oscar', id=2 -------------- where id=1 and nombre='ismael'
        String[] separador = consulta.split(" where ");
        if(separador.length==1){
            JOptionPane.showMessageDialog(null, "Error de sintaxis faltó 'where' en su consulta","Error", ERROR_MESSAGE);
            return;
        }
        //les quitamos ' a los valores
        //ejemplo: nombre='oscar' ====== nombre=oscar
        separador[0]=separador[0].replace("'", "");
        //separamos cada columna con su valor a modificar
        //ejemplo nombre=oscar, id=2 ==================== [0]: nombre=oscar  [1]: id=2
        String[] campVal=separador[0].split(",");
        
        //vamos a encontrar el registro que cumpla con las condiciones
        int [] posicion = where("where "+separador[1], tabla);
        //si no se encontró un registro con esas condiciones se saldrá
        if(posicion==null)
            return;
        
        
        String [] campo=new String[campVal.length];
        String [] valor=new String [campVal.length];
        for(int i=0;i<campVal.length;i++){
            //separaremos el valor del campo
            //ejemplo nombre=oscar ----------------- campo = nombre y valor=oscar
            String[] campValTemp=campVal[i].split("=");
            campo[i]=campValTemp[0];
            valor[i]=campValTemp[1];
        }
        //tomará las posiciones de las columnas que desea modificar
        int[] posicionColumna=seleccionarCamposArchivo(campo,tabla);
        //si las columnas que ingreso el usuario son diferentes a las que estan en el archivo arrojara un error
        if(posicionColumna==null){
            JOptionPane.showMessageDialog(null,"Error en los campos", "Error", ERROR_MESSAGE);
            return;
        }
        
        //traemos todos los datos del archivo
        String[] datosArchivo=leerDatosArchivo(tabla);
        
        //vamos a actualizar todos los campos que se encontraron
        for(int p=0;p<posicion.length;p++){
            //seleccionamos el registro encontrado
            String registro=datosArchivo[posicion[p]];
            //se separaran los datos
            String[] datosRegistro=registro.split(",");
            //se ingresaran los nuevos datos
            for(int i =0;i<valor.length;i++){
                if(posicionColumna[i]!=0)
                    datosRegistro[posicionColumna[i]]=" "+valor[i];
                else
                    datosRegistro[posicionColumna[i]]=""+valor[i];
            }
            //se volvera a concatenar el registro
            registro="";
            for(int i=0;i<datosRegistro.length;i++){
                if(i<datosRegistro.length-1)
                    registro+=datosRegistro[i]+",";
                else
                    registro+=datosRegistro[i];
            }
            //colocamos el registro actualizado 
            datosArchivo[posicion[p]]=registro;
        }
        
        //y lo registramos en el txt
        EscribirArchivo(datosArchivo,tabla);
        
        JOptionPane.showMessageDialog(null,"Registro exitoso");
    }
    
    
    //*******************************************************************************************************
    //******hacemos los procedimientos y validaciones para hacer el select en una tabla**********************
    //*******************************************************************************************************
    public void ConsultaSelectSimple(String consulta,File carpeta){
        //la tabla donde se traeran los registros
        File tabla =null;
        //separamos las columnas de la tabla
        //ejemplo select * from llibros: sería: *, libros
        if(!consulta.contains(" from ")){
            JOptionPane.showMessageDialog(null, "Error de sintaxis '"+consulta+"'", "ERROR", ERROR_MESSAGE);
            return;
        }
        String [] con=consulta.split(" from ");
        //guardamos los campos que deseea ver el usuario
        String camposCon=con[0].replace(" ","");
        
        //los campos que cumplen con la sentencia where
        int [] numRegistros=null;
        
        if(con[1].contains(" where ")){
            //se dividiran la consulta del nombre de la tabla y de la condiciones where
            con=con[1].split(" where ");
            //comprobamos si la tabla existe
            tabla = tablaExist(carpeta,con[0]);
            if(tabla==null)
                return;
            //comprobamos si hay registros que cumplan con la sentencia where
            numRegistros=where("where "+con[1],tabla);
            if(numRegistros==null)
                return;
            //al final de la condicion se regresarán los registros que cumplen con las condiciones y la carpeta
        }else{
            //comprobamos si la tabla existe
            tabla = tablaExist(carpeta,con[1]);
            if(tabla==null)
                return;
        }
        
        String [] registros = registrosWhere(numRegistros,tabla);
        imprimir(registros,camposCon);
    }
    
    
    //*******************************************************************************************************
    //******hacemos los procedimientos y validaciones para hacer el select en una tabla**********************
    //*******************************************************************************************************
    public void consultaSelectInner(String consulta, File carpeta){
        //guardamos los campos del select
        String camposConsulta;
        //guardamos los campos del where
        String campWhere="";
       
        //guardaremos las tablas y los alias del primer inner
        String [] tablasNombre = new String[10];
        File [] tablas =null;
        String [] alias= new String[10];
        String [] onInner = new String[10];
        
        //separamos la consulta del where
        if(consulta.contains(" where ")){
            String []div=consulta.split(" where ");
            campWhere="where " +div[1];            
            consulta=div[0];
        }
        //separamos las columnas de la tabla
        //ejemplo select * from llibros: sería: *, libros
        String [] con=consulta.split(" from ");
        //guardamos los campos que deseea ver el usuario
        camposConsulta=con[0].replace(" ","");
        
        consulta=con[1];
        //veremos cuantos inner son en la consulta
        int contInner=0;
        String conTMP=consulta;
        do{
            conTMP=conTMP.replaceFirst(" inner join ", "");
            contInner++;
        }while(conTMP.contains(" inner join "));
        
        /********************************************************************************************
         ********************vamos a llenar las tablas con sus alias y con sus condiciones***********
         ********************************************************************************************/
        //haremos el llenado de las tablas, alias y sus comparaciones.
        //separamos el primer inner de la consulta
        String [] innerTemp=consulta.split(" inner join ",2);
        //separamos la tabla y su alias para despues insertarlo en sus arreglos
        String [] campAlias=innerTemp[0].split(" as ");
        //agregamos la primera tabla
        tablasNombre[0]=campAlias[0];
        alias[0]=campAlias[1];
        //dejamos el resto de la consulta en su variable natural
        consulta = innerTemp[1];
        onInner[0]="solo";
        //vamos a recorrer todos los inner joins que hay en la tabla
        for(int i=1;i<contInner+1;i++){
            innerTemp= consulta.split(" on ",2);
            //guardamos los alias y el nombre de la tabla
            campAlias=innerTemp[0].split(" as ",2);
            tablasNombre[i]=campAlias[0];
            alias[i]=campAlias[1];
            //validamos que cuando ya se hayan recorrido todos los inner joins y solo quede la ultima tabla
            //con su comparacion se omita este paso
            if(innerTemp[1].contains(" inner join ")){
                //devolvemos lo que sobro de la consulta
                String[] restoConsulta=innerTemp[1].split(" inner join ",2);
                //colocamos las comparaciones en el array
                onInner[i]=restoConsulta[0];
                //devolvemos la consulta
                consulta=restoConsulta[1];
            }else{
                //si ya no quedan mas inner joins se quedará este
                onInner[i]=innerTemp[1];
            }
        }
        //actualizamos la longitud de los arreglos que contienen datos
        tablasNombre=actualizarLength(tablasNombre);
        alias=actualizarLength(alias);
        onInner = actualizarLength(onInner);
        
        
        //comprobamos que las tablas que seleccionó el usuario esten en la Base de datos
        tablas = tablas(tablasNombre,carpeta);
        if(tablas ==null)
            return;
        
        //traer los registros de las tablas y colocarle su alias
        ArrayList registrosTablas = registrosTablas(tablas,alias);
        //traer los registros que cumplan con los inner join
        String [][] resultado = registrosInner(registrosTablas,onInner);
        
        //imprimir(resultado,camposConsulta);
    }
    
    //*******************************************************************************************************
    //*****************en este metodo se seleccionarán los registros que esten en las condiciones de los inner
    //ejemplo: on aut.id=aut2.id; y si tiene mas condiciones como estas solo traera los registros de los inner
    public String[][] registrosInner(ArrayList registrosTablas,String[] onInner){
        String [][] registrosFinales= new String [1000][1000];
        
        String [] idInner = onInner[1].replace(" ", "").split("=");
        System.out.println(idInner[0]+ "----"+idInner[1]);
        String[][] registroTabla1= (String[][]) registrosTablas.get(0);
        String[][] registroTabla2=(String[][]) registrosTablas.get(1);
        
        
        
        
        return registrosFinales;
    }
    
    //*******************************************************************************************************
    //*****vamos a traer los datos de las tablas del inner**********************
    //*******************************************************************************************************
    public ArrayList registrosTablas(File[] tablas, String[] alias){
        ArrayList list = new ArrayList();
        //System.out.println(onInner[1]);
        //ciclo que recorrerá las tablas seleccionadas y las guardara
        //matrices en un arrayList
        for(int i=0;i<tablas.length;i++){
            //leemos los datos de cada tabla
            String [] datosTabla = leerDatosArchivo(tablas[i]);
            //separamos los campos de la tabla para despues colocar los alias
            String [] camposTabla = datosTabla[0].replace(" ", "").split(",");
            //guardaremos los datos en una matriz
            String[][] datosTablaMatriz = new String[1000][1000];
            //ciclo que coloca los alias en los campos de cada tabla
            for(int j=0;j<camposTabla.length;j++){
                datosTablaMatriz[0][j]="";
                //colocamos los compos
                datosTablaMatriz[0][j]+=alias[i]+"."+camposTabla[j];
            }
            for(int j=1;j<datosTabla.length;j++){
                String [] camposTemp = datosTabla[j].split(",");
                for(int o=0;o<camposTemp.length;o++){
                    datosTablaMatriz[j][o]=camposTemp[o].replaceFirst(" ","");
                }
            }
            //vamos a actualizar la longitud de la matriz
            datosTablaMatriz=actualizarLengthMatriz(datosTablaMatriz);
            //agregamos todas las tablas a la lista
            list.add(datosTablaMatriz);
        }
        
        //regresamos los registros de las tablas        
        return list;
    }
    
    
    private void imrpimirMatriz(String [][] registrosTabla){
        for(int j=0;j<registrosTabla.length;j++){
//            for(int h=0;h<registrosTabla[j].length;h++)
//                System.out.println(registrosTabla[j][h]);
            System.out.println(Arrays.toString(registrosTabla[j]));
        }
    }
    //************************************************************************************************
    //*****Imprime los campos que el usuario desea ver, basandose en los registros que encontró******
    //************************************************************************************************
    public void imprimir(String[] registros, String campos){
        if(campos.equals("*")){
            for(int i=0;i<registros.length;i++){
                System.out.println(registros[i]);
            }
            return;
        }
        //separamos los campos que el usuario quiere ver
        String[] camposSelect=campos.replace(" ","").split(",");
        //se separan los campos que se encontraron
        String[] camposRegistro = registros[0].replace(" ","").split(",");
        //guardaremos la posicion de los campos que quiere ver el usuario
        int [] posCamp= new int[camposSelect.length];
        int h=0;
        //conseguimos los campos que son iguales.
        for(int i=0;i<camposSelect.length;i++){
            //recorremos los campos del registro
            for(int j=0;j<camposRegistro.length;j++){
                //comparamos si el campo que el usuario quiere ver esta con los registros encontrados
                if(camposRegistro[j].equals(camposSelect[i])){
                    posCamp[h]=j;
                    h++;
                }
            }
        }
        if(h!=camposSelect.length){
            JOptionPane.showMessageDialog(null, "Uno de los campos '"+campos+"' es incorrecto", "ERROR", ERROR_MESSAGE);
            return;
        }
        
        //colocamos los campos que vamos a imprimir
        for(int i=0;i<camposSelect.length;i++){
            System.out.println(camposSelect[i]+"\t");
        }
        
        //vamos a imprimir los registros con los campos que quiere el usuario
        for(int i=1;i<registros.length;i++){
            //separamos 1 registro a la vez para imprimir el campo que el usuario desea
            camposRegistro=registros[i].split(",");
            //recorremos los campos del registro y se imprime
            for(int j=0;j<h;j++){
                System.out.print(camposRegistro[posCamp[j]]+"\t");
            }
            System.out.println("");
        }
        
        

    }
    
    //************************************************************************************************
    //*****Imprime los campos que el usuario desea ver, basandose en los registros que encontró******
    //************************************************************************************************
    private File [] tablas(String[] nombreTablas,File carpeta){
        File[] tablas=new File[nombreTablas.length];
        File[] archivos = carpeta.listFiles();
        String encontrado="";
        //haremos una validacion si la tabla que el usuario ingreso es correcta
        for(int j=0;j<nombreTablas.length;j++){
            for(int i=0;i<archivos.length;i++){
                //se hace una comparacion con el nombre de la tabla que 
                //el usuario ingresó y los archivos que estan en la carpeta
                if(nombreTablas[j].startsWith(archivos[i].getName().replace(".txt", ""))){
                    //se ingresa el nombre de la tabla a la variable
                    tablas[j]= new File (archivos[i].getAbsolutePath());
                    //identificamos si las carpetas existen
                    encontrado="si";
                    break;
                }else{
                    encontrado="no";
                }
            }
            //sino se encontró una tabla marcará error
            if(encontrado.equals("no")){
                //en caso de que no se encuentre la carpeta marcara error
                JOptionPane.showMessageDialog(null, "El nombre de la tabla '"+nombreTablas[j]+"' es incorrecto","Error", ERROR_MESSAGE);
                return null;
            }
        }
        return tablas;
    }
    
    //************************************************************************************************
    //*****Actualizamos la longitud del arreglo ya que en algunos casos es necesario******
    //************************************************************************************************
    private String [] actualizarLength(String[] arreglo){
        int i=0;
        for(i =0;i<arreglo.length;i++){
            if(arreglo[i]==null)
                break;
        }
        
        String[] nuevoArreglo= new String[i];
        for(int j=0;j<i;j++){
            nuevoArreglo[j]=arreglo[j];
        }
        return nuevoArreglo;
    }
   

    //************************************************************************************************
    //*****Actualizamos la longitud de la matriz ya que en algunos casos es necesario******
    //************************************************************************************************
    private String [][] actualizarLengthMatriz(String[][] matriz){
        int columnas=0;
        int filas=0;
        //veremos cuantas columnas son en total
        for(columnas=0;columnas<matriz.length;columnas++){
            if(matriz[columnas][0]==null)
                break;
        }
        //veremos cuantas filas hay en la matriz
         for(filas=0;filas<matriz[0].length;filas++){
            //verificamos que la matriz no tenga contenido nulo
            if(matriz[0][filas]==null){
                break;
            }
        }
//         System.out.println(columnas+" - "+filas);
        //pasamos la nueva matriz
        String[][] nuevaMatriz= new String[columnas][filas];
        for(int h=0;h<columnas;h++){
            for(int p=0;p<filas;p++)
                nuevaMatriz[h][p]=matriz[h][p];
        }
        return nuevaMatriz;
    }
    
    
    //*******************************************************************************************************
    //**************************************verificamos si la tabla existe***********************************
    //*********metodo que recibe como parametro una consulta, ejemplo: institucion where id_inst=0***********
    private File tablaExist(File carpeta,String consulta){
        File tabla=null;
        //leemos los archivos que estan en la carpeta
        File[] archivos = carpeta.listFiles();
        //haremos una validacion si la tabla que el usuario ingreso es correcta
        for(int i=0;i<archivos.length;i++){
            //se hace una comparacion con el nombre de la tabla que 
            //el usuario ingresó y los archivos que estan en la carpeta
            if(consulta.startsWith(archivos[i].getName().replace(".txt", ""))){
                //se ingresa el nombre de la tabla a la variable
                tabla= new File (archivos[i].getAbsolutePath());
            }
        }
        if(tabla==null){
            //en caso de que no se encuentre la carpeta marcara error
            JOptionPane.showMessageDialog(null, "El nombre de la tabla '"+consulta+"' es incorrecto","Error", ERROR_MESSAGE);
        }
        
        return tabla;
    }
    
    
    //*****************************Recibe los campos a buscar.*************************************
    //Metodo que busca los campos en la tabla para despues regresar las posiciones en las que se encuentran
    //ejemplo, si el usuario ingresa el campo nombre y curp el metodo buscara la posicion en la que se encuentra ese campo
    //ejemplo, la tabla persona tiene los campos: id, nombre, apellidos, domicilio, curp. el metodo regresará el numero
    // que en este caso seria 1 y 4.
    //**************retorna las posiciones donde se encuentran los campos buscados**********************
    private int[] seleccionarCamposArchivo(String[] campos ,File tabla){
        try{//leemos los registros del archivo
            String[] datosArchivo=leerDatosArchivo(tabla);
            //traemos los campos del archivo
            String camposArchivo = datosArchivo[0];
            //les quitamos los espacios
            camposArchivo=camposArchivo.replace(" ", "");
            //separamos los campos
            String[] camposTemp=camposArchivo.split(",");
            //se guardara la posicion de la columna
            int [] posicionCampo=new int[campos.length];
            //contador para saber si los campos que coloco el usuario son iguales a los del archivo
            int cont=0;
            for(int j=0;j<campos.length;j++){
                for(int i=0;i<camposTemp.length;i++){
                    if(camposTemp[i].equalsIgnoreCase(campos[j])){
                        posicionCampo[cont]=i;
                        cont++;
                    }
                }
            }
            if(cont==campos.length){
                return posicionCampo;
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error en las campos");
            return null;
        }
        return null;
    }
    
      
    //*******************************************************************************************************
    //**************este metodo busca los registros que cumplan con la sentencia where***********************
    //****************retorna los registros que cumplen con las condiciones*************************************
    private int [] where(String consulta,File tabla){
        //vamos a separar las condiciones de los valores
        if(consulta.startsWith("where ")){
            consulta=consulta.replaceFirst("where ", "");
        }else{
            JOptionPane.showMessageDialog(null, "Error de sintaxis '"+consulta+"'","Error",ERROR_MESSAGE);
            return null;
        }
        //en este paso haremos un ciclo por si son mas de dos condiciones        
        String[] valCol=consulta.split(" and ");
        //se separa la condicion, contacto='83412312';
        //este arreglo guardará el contacto y el nombre
        String[] numWhere;
        //este arreglo guardará los campoes que son para validar
        String[] camposTemp = new String[valCol.length];
        //este arreglo guardará los valores que serán introducidos
        String[] valoresTemp = new String[valCol.length];
        //recorremos las condiciones del delete, pueden ser 2,3,4, etc... 
        try{
            for(int i=0;i<valCol.length;i++){
                //cada condicion será separada para despues irse guardando
                numWhere=valCol[i].split("=");
                camposTemp[i]=numWhere[0].replace(" ","");
                valoresTemp[i]=numWhere[1].replace("'","");
                valoresTemp[i]=valoresTemp[i].replaceFirst(" ", "");
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Error de sintaxis ","ERROR", ERROR_MESSAGE);
            return null;
        }
        
        //las variables que almacenan las columnas y los valores son 
        //valoresTemp y camposTemp
       
        //**********************esta seccion la haremos para contar las columnas del archivo****************************+
        //vamos a leer los datos del archivo
        String [] datosArchivo=leerDatosArchivo(tabla);
        //vamos a tomar los campos del archivo y les quitamos los espacios
        String camposArch=datosArchivo[0].replace(" ", "");
        //se separan los campos individualmente
        String[] camposArchivo=camposArch.split(",");
        
        //guardaremos la posicion donde los valores son iguales
        int[] columna=new int[6];
        int cont=0;
        //vamos a comparar el campo del archivo
        for(int i =0;i<camposTemp.length;i++){
            //comparamos el campo que el usuario ingreso
            for(int j=0;j<camposArchivo.length;j++){
                //si un campo es igual al que ingreso el usuario, se guardara la posicion
                if(camposTemp[i].equalsIgnoreCase(camposArchivo[j])){
                    columna[cont]=j;
                    cont++;
                }
            }
        }
        
        //como tenemos que el contador sume 1 cada que encuentre el campo en el archivo, 
        //si los campos que agrego el usuario son correctos y se encontraron todos no habra error
        if(cont!=camposTemp.length){
            JOptionPane.showMessageDialog(null, "Columnas no encontradas", "Error", ERROR_MESSAGE);
            return null;
        }
        //usaremos esta variable como identificador de si un registro cumple con el where
        int iguales=0;
        
//haremos un arreglo que guardará la posicion de los registros encontrados que cumplan las condiciones del where
        int [] registrosTemp = new int [100];
        //colocamos una variable que contará los registros que encontró
        int contRegistros=0;
        
        
        //en esta seccion vamos a comparar los valores que ingreso el usuario
        //se compara el valor que ingreso y si se encuentra un valor en el texto
        for(int i=1;i<datosArchivo.length;i++){
            //empezamos desconcatenando los valores de cada registro
            camposArchivo=datosArchivo[i].split(",");
        
            
            //este ciclo comparará las condiciones que tiene la consulta, por ejemplo si tiene dos condiciones
            //se recorrerá dos veces en un solo registro
            for(int j=0;j<cont;j++){
                //comparará los datos que el usuario ingresó con cada registro
                //cambiamos a minusculas los datos y les quitamos espacios al inicio
                camposArchivo[columna[j]]=camposArchivo[columna[j]].toLowerCase().replaceFirst(" ", "");
                if(valoresTemp[j].equals(camposArchivo[columna[j]])){
                    iguales++;
                }
                //si los dos registros cumplen la condicion se eliminara.
                if(iguales==cont){
                    //se guarda la posicion del registro que cumplio con la condicion where
                    registrosTemp[contRegistros]=i;
                    contRegistros++;
                }
            }
            iguales=0;
        }
        if(contRegistros==0){
            JOptionPane.showMessageDialog(null,"Registro no encontrado");
            return null;
        }
        //este arreglo guardará los registros y su longitud será exacta a los registros encontrados
        int [] registros = new int[contRegistros];
        //se pasaran los registros que encontró en un arreglo con la dimencion exacta
        for(int i=0;i<contRegistros;i++){
            registros[i]=registrosTemp[i];
        }
        
        return registros;
    }
    
    
    //*******************************************************************************************************
    //**************en este metodo quitamos los saltos de linea**********************************************
    //*******************************************************************************************************
    public String quitarSaltosLinea(String consulta){
        //con un split indicamos que cada salto de linea se debe quitar
        String[] consultaDiv = consulta.split("\n");
        consulta="";
        //despues que hayamos quitado los saltos se concatenara el String sin saltos
        for(int i =0;i<consultaDiv.length;i++){
            consulta+=consultaDiv[i];
        }
        //se retorna la consulta sin saltos de linea
        return consulta;
    }
    
    
    //*******************************************************************************************************
    //**********************************borraremos el registro del archivo***********************************
    //*******************************************************************************************************
    private void EscribirArchivo(String[] datos,File tabla){
        try{
            //vamos a abrir el archivo para escribir en el
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tabla, false), "UTF8"));
            for(int i=0;i<datos.length;i++){
                out.write(datos[i]);
                if(i<datos.length-1)
                    out.write("\r\n");
            }
            out.close();
        }catch(Exception ex){
            System.out.println("Error al hacer el registro");
        }
    }
    
    //*******************************************************************************************************
    //***************************leeremos los datos del archivo**********************************************
    //*******************************************************************************************************
    private String[] leerDatosArchivo(File tabla){
        try {
            //leemos los datos del archivoo "tabla"
            BufferedReader br = new BufferedReader(new FileReader(tabla.getAbsolutePath()));
            //contamos los registros que tiene el archivo"tabla"
            int cont =0;
            String[]datosArchivo = new String [1000];
            //se ira llenando el arreglo
            while((datosArchivo[cont]=br.readLine())!=null){
                cont++;
            }
            //se cerrara la lectura del archivo
            br.close();
            //se pasaran los registros a otro arreglo por el "lenght"
            String[] datos= new String[cont];
            //entontrará hasta el ultimo registro
            for(int i=0;i<cont;i++){
                datos[i]=datosArchivo[i];
            }
            
            return datos;
        } catch (Exception ex){
            System.out.println("problema en metodo leerDatosArchivo");
            return null;
        } 
    }
   
    //******************************************************************************************
    //*****************se inserta el registro al final del archivo**********************
    //******************************************************************************************
    private boolean Insertar(String consulta,File tabla){
        if(consulta.startsWith(" ")){
            consulta=consulta.replaceFirst(" ", "");
        }
        try{
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tabla, true), "UTF8"));
            out.write("\r\n");
            out.write(consulta);
            //cerramos la conexion
            out.close();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error al insertar un registro","ERROR",ERROR_MESSAGE );
            return false;
        }
        return true;
    }
    
    
    //******************************************************************************************
    //***************** regresa los registros que se encontraron con el where*******************
    //***recibiendo como parametro la posicion de estos y regresando los registros en si*********
    private String[] registrosWhere(int[] numRegistros,File tabla){
        //leemos los registros del archivo
        String [] registros= leerDatosArchivo(tabla);
        //sino hay registros en el where entonces regresamos todos
        if(numRegistros==null){
            return registros;
        }
        //vamos a guardar los registros del where ne este arreglo
        String [] registrosStr=new String [numRegistros.length+1];
        
        //si hay registros que encontró el where entonces pasamos los registros que encontró
        registrosStr[0]=registros[0];
        for(int i=1;i<registrosStr.length;i++){
            registrosStr[i]=registros[numRegistros[i-1]];
        }
        
        return registrosStr;
    }
    
    
    //*******************************************************************************************************
    //**************este metodo busca los registros que cumplan con la sentencia where***********************
    //****************retorna los registros que cumplen con las condiciones*************************************
    private String [] whereInner(String consulta,String [] todosRegistros){
        //vamos a separar las condiciones de los valores
        if(consulta.startsWith("where ")){
            consulta=consulta.replaceFirst("where ", "");
        }else{
            JOptionPane.showMessageDialog(null, "Error de sintaxis '"+consulta+"'","Error",ERROR_MESSAGE);
            return null;
        }
        //en este paso haremos un ciclo por si son mas de dos condiciones        
        String[] valCol=consulta.split(" and ");
        //se separa la condicion, contacto='83412312';
        //este arreglo guardará el contacto y el nombre
        String[] numWhere;
        //este arreglo guardará los campoes que son para validar
        String[] camposTemp = new String[valCol.length];
        //este arreglo guardará los valores que serán introducidos
        String[] valoresTemp = new String[valCol.length];
        //recorremos las condiciones del delete, pueden ser 2,3,4, etc... 
        try{
            for(int i=0;i<valCol.length;i++){
                //cada condicion será separada para despues irse guardando
                numWhere=valCol[i].split("=");
                camposTemp[i]=numWhere[0].replace(" ","");
                valoresTemp[i]=numWhere[1].replace("'","");
                valoresTemp[i]=valoresTemp[i].replaceFirst(" ", "");
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Error de sintaxis ","ERROR", ERROR_MESSAGE);
            return null;
        }
        
        //las variables que almacenan las columnas y los valores son 
        //valoresTemp y camposTemp
       
        //**********************esta seccion la haremos para contar las columnas del archivo****************************+
        //vamos a leer los datos del archivo
        //vamos a tomar los campos del archivo y les quitamos los espacios
        String camposArch=todosRegistros[0].replace(" ", "");
        //se separan los campos individualmente
        String[] camposArchivo=camposArch.split(",");
        
        //guardaremos la posicion donde los valores son iguales
        int[] columna=new int[6];
        int cont=0;
        //vamos a comparar el campo del archivo
        for(int i =0;i<camposTemp.length;i++){
            //comparamos el campo que el usuario ingreso
            for(int j=0;j<camposArchivo.length;j++){
                //si un campo es igual al que ingreso el usuario, se guardara la posicion
                if(camposTemp[i].equalsIgnoreCase(camposArchivo[j])){
                    columna[cont]=j;
                    cont++;
                }
            }
        }
        
        //como tenemos que el contador sume 1 cada que encuentre el campo en el archivo, 
        //si los campos que agrego el usuario son correctos y se encontraron todos no habra error
        if(cont!=camposTemp.length){
            JOptionPane.showMessageDialog(null, "Columnas no encontradas", "Error", ERROR_MESSAGE);
            return null;
        }
        //usaremos esta variable como identificador de si un registro cumple con el where
        int iguales=0;
        
        
        //colocamos una variable que contará los registros que encontró
        int contRegistros=1;
//haremos un arreglo que guardará la posicion de los registros encontrados que cumplan las condiciones del where        
        String [] totalRegistros=new String[1000];
        totalRegistros[0]=todosRegistros[0];
        //en esta seccion vamos a comparar los valores que ingreso el usuario
        //se compara el valor que ingreso y si se encuentra un valor en el texto
        for(int i=1;i<todosRegistros.length;i++){
            //empezamos desconcatenando los valores de cada registro
            camposArchivo=todosRegistros[i].split(",");
        
            
            //este ciclo comparará las condiciones que tiene la consulta, por ejemplo si tiene dos condiciones
            //se recorrerá dos veces en un solo registro
            for(int j=0;j<cont;j++){
                //comparará los datos que el usuario ingresó con cada registro
                //cambiamos a minusculas los datos y les quitamos espacios al inicio
                camposArchivo[columna[j]]=camposArchivo[columna[j]].toLowerCase().replaceFirst(" ", "");
                if(valoresTemp[j].equals(camposArchivo[columna[j]])){
                    iguales++;
                }
                //si los dos registros cumplen la condicion se eliminara.
                if(iguales==cont){
                    //se guarda la posicion del registro que cumplio con la condicion where
                    totalRegistros[contRegistros]=todosRegistros[i];
                    contRegistros++;
                }
            }
            iguales=0;
        }
        if(contRegistros==0){
            JOptionPane.showMessageDialog(null,"Registro no encontrado");
            return null;
        }
        //este arreglo guardará los registros y su longitud será exacta a los registros encontrados
        String [] registros = new String[contRegistros];
        //se pasaran los registros que encontró en un arreglo con la dimencion exacta
        for(int i=0;i<contRegistros;i++){
            registros[i]=totalRegistros[i];
        }
        
        return registros;
    }
    
    
    //pasar de array a matriz, recibe los registros en forma de arreglo y los convierte a matriz
    public String[][] arrayToMatriz(String [] arreglo){
        //hacemos la matriz principal que almacenará los registros temporales
        String [][] matrizTemp= new String[1000][1000];
        int fila=0;
        int columna=0;
        //separamos los registros que hay en el arreglo
        for(int i=0;i<arreglo.length;i++){
            String [] separador=arreglo[i].split(", ");
            for(int j=0;j<separador.length;j++){
                matrizTemp[i][j]=separador[j];
                if(i==0)
                    columna++;
            }
            fila++;
        }
        //pasamos los registros completos a la matriz
        String[][] matriz = new String [fila][columna];
        //se cambia de matriz
        for(int i=0;i<fila;i++)
            for(int j=0;j<columna;j++)
                matriz[i][j]=matrizTemp[i][j];
        //retornamos la matriz
        return matriz;
    }
}
