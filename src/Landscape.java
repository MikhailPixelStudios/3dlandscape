/**
 * Created by mikha on 11.07.2017.
 */
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import static java.lang.Math.*;
public class Landscape extends GLCanvas implements GLEventListener,KeyListener  {
    private static String TITLE = "JOGL 2.0 Setup (GLCanvas)";  // window's title
    private static final int CANVAS_WIDTH = 1920;  // width of the drawable
    private static final int CANVAS_HEIGHT = 1080; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second
    static int w=40;
    static int h=40;
    public static String cc=" ";
    public static JFrame frame = new JFrame();
    static double []eye = new double[3];
    static double[] center = new double[3];
    public static int[][] n=new int[w][h];
    public static void main(String args[]){

        // dots=new dot[1000000];

        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                n=   genworld(n,w,h,2);
                for (int i=0;i<w;i++){
                    for (int j=0;j<h;j++){
                        System.out.print(n[i][j]);
                    }
                    System.out.println();
                }
                eye[0]=1.5;
                eye[2]=1.5;
              //  eye[1]=10;
                // Create the OpenGL rendering canvas
                GLCanvas canvas = new Landscape();
                canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

                // Create a animator that drives canvas' display() at the specified FPS.
                final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

                // Create the top-level container
                // Swing's JFrame or AWT's Frame
                frame.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode()==KeyEvent.VK_W){
                            cc="w";
                        }
                        if (e.getKeyCode()==KeyEvent.VK_A){
                            cc="a";
                        }
                        if (e.getKeyCode()==KeyEvent.VK_S){
                            cc="s";
                        }
                        if (e.getKeyCode()==KeyEvent.VK_D){
                            cc="d";
                        }
                        if (e.getKeyCode()==KeyEvent.VK_DOWN){
                            cc="down";
                        }
                        if (e.getKeyCode()==KeyEvent.VK_LEFT){
                            cc="left";
                        }
                        if (e.getKeyCode()==KeyEvent.VK_RIGHT){
                            cc="right";
                        }
                        if (e.getKeyCode()==KeyEvent.VK_UP){
                            cc="up";
                        }
                    }


                    @Override
                    public void keyReleased(KeyEvent e) {
                        cc=" ";
                    }
                });
                frame.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                    }

                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        super.mouseWheelMoved(e);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {

                    }
                });
                frame.getContentPane().add(canvas);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Use a dedicate thread to run the stop() to ensure that the
                        // animator stops before program exits.
                        new Thread(() -> {
                            if (animator.isStarted()) animator.stop();
                            System.exit(0);
                        }).start();
                    }
                });
                frame.setTitle(TITLE);
                frame.pack();
                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }
    public Landscape() {
        this.addGLEventListener(this);
    }
    static Texture ground,water,tree,sky,wall;

    public GLU glu;
    @Override
    public void init(GLAutoDrawable drawable) {
        n=   genworld(n,w,h,5);
        for (int i=0;i<w;i++){
            for (int j=0;j<h;j++){
                System.out.print(n[i][j]);
            }
            System.out.println();
        }
        GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0f); // set background (clear) color
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting

        URL textureURL;
        textureURL = getClass().getClassLoader().getResource("wall.jpg");
        if (textureURL != null) {
//					textures[i] = TextureIO.newTexture(textureURL, true, null);  // Alternative loader, gives upside down textures!
            BufferedImage img = null;
            try {
                img = ImageIO.read(textureURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageUtil.flipImageVertically(img);
            wall = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
            wall.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            wall.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            wall.enable(gl);
            // inittexture(gl, "ground.jpg", ground);
        }

        URL textureURL2;
        textureURL2 = getClass().getClassLoader().getResource("grass.jpg");
        if (textureURL != null) {
//					textures[i] = TextureIO.newTexture(textureURL, true, null);  // Alternative loader, gives upside down textures!
            BufferedImage img = null;
            try {
                img = ImageIO.read(textureURL2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageUtil.flipImageVertically(img);
            ground = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
            ground.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            ground.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            ground.enable(gl);
            //  inittexture(gl, "ground.jpg", ground);

            textureURL2 = getClass().getClassLoader().getResource("sky.jpg");
            if (textureURL != null) {
//					textures[i] = TextureIO.newTexture(textureURL, true, null);  // Alternative loader, gives upside down textures!
                img = null;
                try {
                    img = ImageIO.read(textureURL2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageUtil.flipImageVertically(img);
                sky = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
                sky.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                sky.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                sky.enable(gl);
            }

            textureURL2 = getClass().getClassLoader().getResource("water.gif");
            if (textureURL != null) {
//					textures[i] = TextureIO.newTexture(textureURL, true, null);  // Alternative loader, gives upside down textures!
                img = null;
                try {
                    img = ImageIO.read(textureURL2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageUtil.flipImageVertically(img);
                water = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
                water.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                water.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                water.enable(gl);
            }
            textureURL2 = getClass().getClassLoader().getResource("tree.jpg");
            if (textureURL != null) {
//					textures[i] = TextureIO.newTexture(textureURL, true, null);  // Alternative loader, gives upside down textures!
                img = null;
                try {
                    img = ImageIO.read(textureURL2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageUtil.flipImageVertically(img);
                tree = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
                tree.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                tree.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                tree.enable(gl);
            }
        }

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    public static void drawground(GL2 gl, int x, float y, int z){
        ground.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glEnable(GL_TEXTURE_2D);
        ground.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        ground.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glVertex3f(-x, -y, -z);//001
        gl.glTexCoord2f(0,100);
        gl.glVertex3f(-x ,-y, z);//011
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x, -y, z);//111
        gl.glTexCoord2f(100,0);
        gl.glVertex3f(x, -y, -z);//101
        gl.glTexCoord2f(100,100);
        gl.glEnd();
    }
    public static void landscape(GL2 gl,float x,float y,float z, int[][]n,int w,int h){
        float Zoom = 3;

        ground.bind(gl);
        for (int i= 0;i<w-1;i++) // Часть кода из Terrain.Cpp
        {
            for (int j=0;j<h-1;j++)
            {
                x=i*Zoom;
                y=j*Zoom;

                gl.glBegin(GL_TRIANGLE_STRIP);
                gl.glTexCoord2f(0,0);
                gl.glVertex3f(x,z+n[i][j], y);
                gl.glTexCoord2f(0,1);
                gl.glVertex3f(x+Zoom,z+n[i+1][j], y );
                gl.glTexCoord2f(1,0);
                gl.glVertex3f(x,z+n[i][j+1], y+Zoom );
                gl.glTexCoord2f(1,1);
                gl.glVertex3f(x+Zoom,z+n[i+1][j+1], y+Zoom );

                gl.glEnd();
            }
        }
    }
    public void drawcube(GL2 gl,int x,float y, int z, float r,Texture t){
        t.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        //  gl.glColor3f( 1f,0f,1f );
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y, z+r);//001
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x, y+r, z+r);//011
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y+r, z+r);//111
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(x+r, y, z+r);//101

        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        //gl.glColor3f( 0,0f,1f );
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y, z+r);//001
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x, y+r, z+r);//011
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x, y+r, z);//010
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(x, y, z);//000

        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        //gl.glColor3f( 1f,1f,1f );
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y+r, z+r);//011
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x+r, y+r, z+r);//111
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y+r, z);//110
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(x, y+r, z);//010

        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        //gl.glColor3f( 1f,0f,0 );
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y, z+r);//001
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x+r, y, z+r);//101
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y, z);//100
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(x, y, z);//000

        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);

        //gl.glColor3f( 1f,0f,2 );
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x+r, y, z+r);//101
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x+r, y+r, z+r);//111
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y+r, z);//110
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(x+r, y, z);//100

        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        //gl.glColor3f( 1f,0f, (float) 0.5);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y, z);//000
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x, y+r, z);//010
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y+r, z);//110
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(x+r, y, z);//100

        gl.glEnd();
    }
    public void drawtree(GL2 gl,float x,float y, float z, float r,Texture t){
        t.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y, z);//000
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x, y, z+r);//001
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y, z+r);//101
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(x+r, y, z);//100
        gl.glEnd();
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y, z);//000
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x+0.5f*r, y+0.5f*r, z+0.5f*r);//0.50.50.5
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x, y, z+r);//001
        gl.glEnd();
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x+r, y, z+r);//101
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x+0.5f*r, y+0.5f*r, z+0.5f*r);//0.50.50.5
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x, y, z+r);//001
        gl.glEnd();
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x+r, y, z);//100
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x+0.5f*r, y+0.5f*r, z+0.5f*r);//0.50.50.5
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y, z+r);//101
        gl.glEnd();
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(x, y, z);//000
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(x+0.5f*r, y+0.5f*r, z+0.5f*r);//0.50.50.5
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(x+r, y, z);//100
        gl.glEnd();
    }
    static void obworld(int[][] world, int x,int y,int param){
       int r =  (int) (Math.random()*(param+3));
       world[x][y]=r;
    }
static class worlds{
       public  int x,y,n;
    }
    static int[][] genworld(int[][] world, int w, int h, int seed){
        worlds[] wo=new worlds[seed];


        world=new int[w][h];

        for (int i=0;i<w;i++){
            for (int j=0; j<h; j++){
                world[i][j]=0;
            }
        }
        for (int i=0;i<seed;i++){
            wo[i]=new worlds();
            int x = 0 + (int)(Math.random() * (w-1));
            int y = 0 + (int)(Math.random() * (h-1));
            wo[i].x=x;
            wo[i].y=y;
            wo[i].n=  (int) (Math.random()*18-8);
            world[wo[i].x][wo[i].y]=wo[i].n;
        }


        for (int i=0;i<w;i++){
            for (int j=0; j<h; j++){
                if (world[i][j]!=0){
                   if (i-1>=0 && j-1>=0) obworld(world,i-1,j-1,world[i][j]);
                    if (i-1>=0 ) obworld(world,i-1,j,world[i][j]);
                    if (j-1>=0 ) obworld(world,i,j-1,world[i][j]);
                    if (j+1<h ) obworld(world,i,j+1,world[i][j]);
                    if (i+1<w ) obworld(world,i+1,j,world[i][j]);
                    if (i+1<w && j+1<h) obworld(world,i+1,j+1,world[i][j]);
                    if (i-1>=0 && j+1<h) obworld(world,i-1,j+1,world[i][j]);
                    if (i+1<w && j-1>=0) obworld(world,i+1,j-1,world[i][j]);
                }
            }
        }
        return world;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
       // glut = new GLUT();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

        gl.glLoadIdentity();  // reset the model-view matrix

        // ----- Your OpenGL rendering code here (Render a white triangle for testing) -----
        // gl.glClearColor(1,1,255,0);
        //  gl.glRotatef(10,0,1,0);
        double height=0.3;

        glu.gluLookAt(eye[0],eye[1]+height,eye[2],center[0],center[1]+height,center[2], 0, cos(angleY), 0);  // translate into the screen
        /*gl.glBegin(GL_TRIANGLES); // draw using triangles
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glEnd();*/

        switch (cc) {
            case "w": {
                    boolean b = true;
                    oldeye[0]=eye[0];
                    oldeye[2]=eye[2];
                    eye[0] += 0.05 * cos(angleX);
                    eye[2] += 0.05 * sin(angleX);
                    center[0] = eye[0] + cos(angleX);

                    center[2] = eye[2] + sin(angleX);


            }
            break;

            case "s": {


                    oldeye[0]=eye[0];
                    oldeye[2]=eye[2];
                    eye[0] -= 0.05 * cos(angleX);
                    eye[2] -= 0.05 * sin(angleX);
                    center[0] = eye[0] + cos(angleX);

                    center[2] = eye[2] + sin(angleX);

            }
            break;
            case "a": {


                    oldeye[0]=eye[0];
                    oldeye[2]=eye[2];
                    eye[0] += 0.05 * sin(angleX);
                    eye[2] -= 0.05 * cos(angleX);
                    center[0] = eye[0] + cos(angleX);

                    center[2] = eye[2] + sin(angleX);

            }
            break;

            case "d": {


                    oldeye[0]=eye[0];
                    oldeye[2]=eye[2];
                    eye[0] -= 0.05 * sin(angleX);
                    eye[2] += 0.05 * cos(angleX);
                    center[0] = eye[0] + cos(angleX);

                    center[2] = eye[2] + sin(angleX);

            }
            break;
            case "up": {

                angleY += 0.1;
                center[0] = eye[0] + cos(angleY)*cos(angleX);
                center[1] = eye[1] + sin(angleY);
                center[2]=eye[2]+cos(angleY)*sin(angleX);

            }
            break;
            case "down": {

                angleY -= 0.1;
                center[0] = eye[0] + cos(angleY)*cos(angleX);
                center[1] = eye[1] + sin(angleY);
                center[2]=eye[2]+cos(angleY)*sin(angleX);
            }
            break;
            case "right": {
                angleX += 0.1;
                center[0] = eye[0] + cos(angleX);

                center[2] = eye[2] + sin(angleX);

            }
            break;
            case "left": {
                angleX -= 0.1;
                center[0] = eye[0] + cos(angleX);

                center[2] = eye[2] + sin(angleX);

            }
            break;
        }

      //  drawground(gl,100,0,100);
        drawcube(gl,-100,-100,-100,400,sky);
        landscape(gl,0,0,0,n,w,h);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) height = 1;   // prevent divide by zero
        float aspect = (float)width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(45.0, aspect, 0.1, 1000.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }
    int and=0;
    static float angleX=0;
    static float angleY=0;
    static float angleZ=0;
    double [] oldeye = new double[3];

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
