package sort;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Sort {

  int[] numeros;

  public Sort(String archivo, int framerate, String metodo) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          JFrame frame = new JFrame("Ordenamientos");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setLayout(new BorderLayout());
          frame.add(new Contenedor(archivo, framerate, metodo));
          frame.pack();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
        } catch (Exception e) {
          System.out.println("\t:(");
        }
      }
    });
  }

  public class Contenedor extends JPanel {

    private JLabel etiqueta;

    public Contenedor(String archivo, int framerate, String metodo) {
      setLayout(new BorderLayout());
      etiqueta = new JLabel(new ImageIcon(createImage(archivo)));
      add(etiqueta);
      JButton botonOrdenar = new JButton("Ordenar");
      add(botonOrdenar, BorderLayout.SOUTH);
      botonOrdenar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          BufferedImage imagen = (BufferedImage) ((ImageIcon) etiqueta.getIcon()).getImage();
          new UpdateWorker(imagen, etiqueta, archivo, framerate, metodo).execute();
        }
      });

    }

    public BufferedImage createImage(String archivo) {
      BufferedImage imagen = null;
      try {
        imagen = ImageIO.read(new File("resource/" + archivo));
        ataqueHackerman(imagen);
        Graphics2D g = imagen.createGraphics();
        g.dispose();
      } catch (Exception e) {
        System.err.println("(-)\tAsegurate de estar en el directorio 'src'");
        System.err.println("\ty de haber escrito bien el nombre de imagen (la cual debe estar en la carpeta resource)");
      }
      return imagen;
    }

    public void ataqueHackerman(BufferedImage imagen) {
      int length = imagen.getHeight() * imagen.getWidth();
      numeros = new int[length];
      for (int i = 0; i < numeros.length; i++)
        numeros[i] = i;
      Random r = new Random();
      for (int i = 0; i < length; i++) {
        int j = r.nextInt(length);
        swapImagen(imagen, i, j);
      }
    }

    public void swapImagen(BufferedImage imagen, int i, int j) {
      int colI = i % imagen.getWidth();
      int renI = i / imagen.getWidth();
      int colJ = j % imagen.getWidth();
      int renJ = j / imagen.getWidth();
      int aux = imagen.getRGB(colI, renI);
      imagen.setRGB(colI, renI, imagen.getRGB(colJ, renJ));
      imagen.setRGB(colJ, renJ, aux);
      aux = numeros[i];
      numeros[i] = numeros[j];
      numeros[j] = aux;
    }

  }

  public class UpdateWorker extends SwingWorker<BufferedImage, BufferedImage> {

    private BufferedImage referencia;
    private BufferedImage copia;
    private JLabel target;
    int framerate;
    int n;
    String metodo;
    int iteracion;

    public UpdateWorker(BufferedImage master, JLabel target, String archivo, int speed, String algoritmo) {
      this.target = target;
      try {
        referencia = ImageIO.read(new File("resource/" + archivo));
        copia = master;
        n = copia.getHeight() * copia.getWidth();
      } catch (Exception e) {
        System.err.println(":c Esto no deberia ocurrir");
      }
      framerate = speed; // Indica cada cuantas iteraciones se actualizara la imagen
      metodo = algoritmo;
      iteracion = 0;
    }

    public BufferedImage updateImage() {
      Graphics2D g = copia.createGraphics();
      g.drawImage(copia, 0, 0, null);
      g.dispose();
      return copia;
    }

    @Override
    protected void process(List<BufferedImage> chunks) {
      target.setIcon(new ImageIcon(chunks.get(chunks.size() - 1)));
    }

    public void update() {
      for (int i = 0; i < n; i++) {
        int indiceDeOriginal = numeros[i];
        int colOriginal = indiceDeOriginal % copia.getWidth();
        int renOriginal = indiceDeOriginal / copia.getWidth();
        int colI = i % copia.getWidth();
        int renI = i / copia.getWidth();
        copia.setRGB(colI, renI, referencia.getRGB(colOriginal, renOriginal));
      }
      publish(updateImage());
    }

    @Override
    protected BufferedImage doInBackground() throws Exception {
      if (metodo.equals("bubble"))
        bubbleSort();
      if (metodo.equals("selection"))
        selectionSort();
      if (metodo.equals("insertion"))
        insertionSort();
      if (metodo.equals("merge"))
        mergeSort();
      if (metodo.equals("quick"))
        quickSort();
      update();
      return null;
    }

    private void bubbleSort() {
      for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
          if (numeros[j] > numeros[j + 1])
            swap(j, j + 1);
        }
        if (iteracion % framerate == 0)
          update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones
                    // deseadas
        iteracion = (iteracion + 1) % framerate; // Aumentamos el numero de iteraciones
      }
    }

    private void selectionSort() {
      for (int i = 0; i < n - 1; i++) {
        int minIndex = i;
        for (int j = i + 1; j < n; j++) {
          if (numeros[j] < numeros[minIndex]) {
            minIndex = j;
          }
        }
        swap(i, minIndex);
        if (iteracion % framerate == 0)
          update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones
                    // deseadas
        iteracion = (iteracion + 1) % framerate; // Aumentamos el numero de iteraciones
      }
    }

    private void insertionSort() {
      for (int i = 1; i < n; i++) {
        int key = numeros[i];
        int j = i - 1;
        for (j = i - 1; j >= 0 && numeros[j] > key; j--) {
          numeros[j + 1] = numeros[j];
        }
        numeros[j + 1] = key;
        if (iteracion % framerate == 0)
          update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones
                    // deseadas
        iteracion = (iteracion + 1) % framerate; // Aumentamos el numero de iteraciones
      }
    }

    private void mergeSort() {
      mergeSort(0, n - 1);
    }

    private void mergeSort(int low, int high) {
      if (low < high) {
        int mid = (low + high) / 2;
        mergeSort(low, mid);
        mergeSort(mid + 1, high);
        merge(low, mid, high);
        if (iteracion % framerate == 0)
          update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones
                    // deseadas
        iteracion = (iteracion + 1) % framerate; // Aumentamos el numero de iteraciones
      }
    }

    private void merge(int low, int mid, int high) {
      int n1 = mid - low + 1;
      int n2 = high - mid;

      int left[] = new int[n1];
      int right[] = new int[n2];

      for (int k = 0; k < n1; k++) {
        left[k] = numeros[low + k];
      }

      for (int k = 0; k < n2; k++) {
        right[k] = numeros[mid + 1 + k];
      }

      int i = 0;
      int j = 0;
      int k = low;

      while (i < n1 && j < n2) {
        if (left[i] <= right[j]) {
          numeros[k] = left[i];
          i++;
        } else {
          numeros[k] = right[j];
          j++;
        }
        k++;
      }

      while (i < n1) {
        numeros[k] = left[i];
        i++;
        k++;
      }

      while (j < n2) {
        numeros[k] = right[j];
        j++;
        k++;
      }
    }

    private void quickSort() {
      quicksort(0, n - 1);
    }

    private void quicksort(int l, int h) {
      if (l < h) {
        int p = partition(l, h);
        if (iteracion % framerate == 0)
          update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones
                    // deseadas
        iteracion = (iteracion + 1) % framerate; // Aumentamos el numero de iteraciones
        quicksort(l, p - 1);
        quicksort(p + 1, h);
      }
    }

    private int partition(int l, int h) {
      int pivot = numeros[h];
      int i = l - 1;

      for (int j = l; j < h; j++) {
        if (numeros[j] <= pivot) {
          i++;
          swap(i, j);
        }
      }
      swap(i + 1, h);
      return i + 1;
    }

    public void swap(int i, int j) {
      int aux = numeros[i];
      numeros[i] = numeros[j];
      numeros[j] = aux;
    }

  }

}
