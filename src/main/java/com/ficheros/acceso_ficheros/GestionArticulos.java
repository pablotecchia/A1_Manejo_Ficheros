package com.ficheros.acceso_ficheros;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import com.opencsv.CSVWriter;


public class GestionArticulos 
{
	
	private static ArrayList<Articulo> listaArticulos = new ArrayList<>();
	
    public static void main( String[] args )
    {
    	cargarArticulosDesdeArchivo();

        Scanner scanner = new Scanner(System.in);

        int opcion;
        do {
            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    agregarArticulo(scanner);
                    break;
                case 2:
                    borrarArticulo(scanner);
                    break;
                case 3:
                    consultarArticulo(scanner);
                    break;
                case 4:
                    listarArticulos();
                    break;
                case 5:
                    terminarPrograma();
                    break;
                    
                case 6:
                	escribirCSV(listaArticulos,"lista.csv");
                	break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        } while (opcion != 5);

        scanner.close();
    }

    private static void cargarArticulosDesdeArchivo() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("articulos.dat"))) {
            listaArticulos = (ArrayList<Articulo>) ois.readObject();
            System.out.println("Artículos cargados desde el archivo.");
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe. Se creará uno nuevo al finalizar el programa.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("Menú:");
        System.out.println("1. Añadir nuevo artículo");
        System.out.println("2. Borrar artículo por id");
        System.out.println("3. Consultar artículo por id");
        System.out.println("4. Listado de todos los artículos");
        System.out.println("5. Terminar el programa");
        System.out.println("6. Exportar Lista en CSV");
        System.out.print("Selecciona una opción: ");
    }

    private static void agregarArticulo(Scanner scanner) {
    	boolean entradaValida = false;

        do {
            try {
            	System.out.print("Ingrese el ID del artículo: ");
                int id = scanner.nextInt();

                // Verificar si el ID ya existe
                if (existeArticuloConId(id)) {
                    System.out.println("Error: Ya existe un artículo con ese ID. Ingrese un ID único.");
                    continue; // Salta al siguiente ciclo del bucle
                }

                System.out.print("Ingrese el nombre del artículo: ");
                String nombre = scanner.next();

                System.out.print("Ingrese la descripción del artículo: ");
                String descripcion = scanner.next();

                System.out.print("Ingrese el stock del artículo: ");
                int stock = scanner.nextInt();

                System.out.print("Ingrese el precio del artículo: ");
                double precio = scanner.nextDouble();

                Articulo nuevoArticulo = new Articulo(id, nombre, descripcion, stock, precio);
                listaArticulos.add(nuevoArticulo);

                System.out.println("Artículo agregado correctamente.");
                entradaValida = true; 

            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Tipo de entrada incorrecto. Asegúrate de ingresar el formato correcto.");
                scanner.nextLine(); 
            }
        } while (!entradaValida);
    }

    private static void borrarArticulo(Scanner scanner) {
        System.out.print("Ingrese el ID del artículo que desea borrar: ");
        int id = scanner.nextInt();

        // Buscar el artículo en la lista por su ID
        Articulo articuloAEliminar = null;
        for (Articulo articulo : listaArticulos) {
            if (articulo.getId() == id) {
                articuloAEliminar = articulo;
                break;
            }
        }

        // Eliminar el artículo si se encontró
        if (articuloAEliminar != null) {
            listaArticulos.remove(articuloAEliminar);
            System.out.println("Artículo eliminado correctamente.");
        } else {
            System.out.println("No se encontró un artículo con ese ID.");
        }
    }

    private static void consultarArticulo(Scanner scanner) {
        System.out.print("Ingrese el ID del artículo que desea consultar: ");
        int id = scanner.nextInt();

        // Buscar el artículo en la lista por su ID
        Articulo articuloConsultado = null;
        for (Articulo articulo : listaArticulos) {
            if (articulo.getId() == id) {
                articuloConsultado = articulo;
                break;
            }
        }

        // Mostrar la información del artículo si se encontró
        if (articuloConsultado != null) {
            System.out.println("Información del artículo:");
            System.out.println(articuloConsultado);
        } else {
            System.out.println("No se encontró un artículo con ese ID.");
        }
    }

    private static void listarArticulos() {
        System.out.println("Listado de todos los artículos:");
        for (Articulo articulo : listaArticulos) {
            System.out.println(articulo);
        }
    }

    private static void terminarPrograma() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("articulos.dat"))) {
            oos.writeObject(listaArticulos);
            System.out.println("Artículos guardados en el archivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static boolean existeArticuloConId(int id) {
        for (Articulo articulo : listaArticulos) {
            if (articulo.getId()== id) {
                return true;
            }
        }
        return false;
    }
    
    public static String escribirCSV(ArrayList<Articulo> listaArticulos, String rutaArchivo) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(rutaArchivo));

            // Escribe la cabecera
            String[] cabecera = {"ID", "Nombre", "Descripción", "Stock", "Precio"};
            writer.writeNext(cabecera);

            // Escribe los datos de los artículos
            for (Articulo articulo : listaArticulos) {
                String[] datos = {
                        String.valueOf(articulo.getId()),
                        articulo.getNombre(),
                        articulo.getDescripcion(),
                        String.valueOf(articulo.getStock()),
                        String.valueOf(articulo.getPrecio())
                };
                writer.writeNext(datos);
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("Error de acceso al archivo CSV.");
            return "error";
        }
        System.out.println("Exportación exitosa a: " + rutaArchivo);
        return "ok";
    }
    
}