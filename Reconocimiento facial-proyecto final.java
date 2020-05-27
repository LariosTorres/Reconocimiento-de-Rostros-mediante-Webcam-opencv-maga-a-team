# Reconocimiento-de-Rostros-mediante-Webcam-opencv-maga-a-team
/*
 * Creado por:
 * Magaña Varela Diego Ivan
 * Jose de Jesus Larios Torres
 * Larino David
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
/*
 * aqui se importan los componentes de la libreria de OpenCV*/
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
 
class PanelDeRostros extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage imagen;
 
	public PanelDeRostros() {
		super();
	}
 
	/*
	 * Convierte y escribe una Matriz en un objeto BufferedImage
	 */
	public boolean convierteMatABufferedImage(Mat matriz) {
		MatOfByte mb = new MatOfByte();
		Imgcodecs.imencode("ima.jpg", matriz, mb);
		try {
			this.imagen = ImageIO.read(new ByteArrayInputStream(mb.toArray()));
		} catch (IOException e) {
			e.printStackTrace();
			return false; // error
		}
		return true; // éxito
	}
 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.imagen == null)
			return;
		g.drawImage(this.imagen, 10, 10, this.imagen.getWidth(), this.imagen.getHeight(), null);
	}
}
 
class DetectorRostros {
	private CascadeClassifier clasificador;
 
	public DetectorRostros() {
		/*Se lee el archivo Haar que le permite a OpenCV detectar rostros frontales en una imagen
		 * este es el algorithmo de Machine Learning
		 * */
		clasificador = new CascadeClassifier("C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");
		if (clasificador.empty()) {
			System.out.println("Error.");
			return;
		} else {
			System.out.println("Face detector readed.");
		}
	}
 
	public Mat detecta(Mat frameDeEntrada) {
		Mat mRgba = new Mat();
		Mat mGrey = new Mat();
		MatOfRect rostros = new MatOfRect();
		frameDeEntrada.copyTo(mRgba);
		frameDeEntrada.copyTo(mGrey);
		Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(mGrey, mGrey);
		clasificador.detectMultiScale(mGrey, rostros);
		System.out.println(String.format("Faces Detected %s", rostros.toArray().length));
		for (Rect rect : rostros.toArray()) {
			//Se dibuja un rectángulo donde se ha encontrado el rostro
			Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0));
 		}
		return mRgba;
	}
}
 
 
public class Principal {
 
	public static void main(String arg[]) throws InterruptedException {
		// Leyendo librería nativa
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
 
		// Se crea el JFrame
		JFrame frame = new JFrame("Face detection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
		DetectorRostros detectorRostros = new DetectorRostros();
		PanelDeRostros panel = new PanelDeRostros();
		frame.setSize(400, 400);
		frame.setBackground(Color.BLUE);
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
 
		// Se crea una matriz que contendrá la imagen
		Mat imagenDeWebCam = new Mat();
		VideoCapture webCam = new VideoCapture(0);
 
		if (webCam.isOpened()) {
			Thread.sleep(500); // Se interrumpe el thread para permitir que la webcam se inicialice
			while (true) {
				webCam.read(imagenDeWebCam);
				if (!imagenDeWebCam.empty()) {
					Thread.sleep(200); // Permite que la lectura se complete
					frame.setSize(imagenDeWebCam.width() + 40, imagenDeWebCam.height() + 60);
					// Invocamos la rutina de opencv que detecta rostros sobre la imagen obtenida por la webcam
					imagenDeWebCam = detectorRostros.detecta(imagenDeWebCam);
					// Muestra la imagen
					panel.convierteMatABufferedImage(imagenDeWebCam);
					panel.repaint();
				} else {
					System.out.println("No faces detected");
					break;
				}
			}
		}
		webCam.release(); // Se libera el recurso de la webcam
	}
}
