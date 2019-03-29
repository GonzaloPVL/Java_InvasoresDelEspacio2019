
package codigo;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Gonzalo-Chus
 */
public class Marciano {
    
    public Image imagen1, imagen2 = null;
    public int x = 0;
    public int y = 30;
    public int vX = 2;
    public boolean vivo = true;
    
    
    public Marciano(){
        
    }
    
    public void mueve(){
        
        x +=vX;
    }

    public void setvX(int vX) {
        this.vX = vX;
    }

    public int getvX() {
        return vX;
    }
}
