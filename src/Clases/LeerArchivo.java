package Clases;

import Interfaces.Home;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class LeerArchivo {
    
    public LeerArchivo(File carpeta){
        //colocamos los archivos seleccionados en un arreglo de documentos
        File[] listaArchivos = carpeta.listFiles();
        //vamos a ingresar los documentos que sean txt solamente al arreglo de archivos txt
        File[] listaArchivosTxtTemp = new File[10];
        int cont=0;
        //recorremos todos los archivos para separar los archivos txt de otro tipo de archivos
        for (int i = 0; i < listaArchivos.length; i++){   
            //si el archivo es txt entonces se guarda en el arreglo
            if (listaArchivos[i].getName().endsWith(".txt")){
                listaArchivosTxtTemp[cont]=listaArchivos[i];
                cont++;
            }
        }
        //una vez los archivos ya esten separados se agregaran en una lista de archivos de texto solamente
        File[] listaArchivosTxt =new File[cont];
        for(int i=0;i<cont;i++){
            listaArchivosTxt[i]=listaArchivosTxtTemp[i];
            System.out.println(listaArchivosTxt[i].getName());
        }
    }
}
