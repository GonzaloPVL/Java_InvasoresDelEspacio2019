import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author xp
 */
public class Explosion {
    public Image imagen1 = null;
    public int x = 0;
    public int y = 0;
    public int vX = 1;
    public boolean vivo = true;
    
    
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
