
package JustRun_130854;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;

    
/**
 * @author Giuliano Cesari Tacioli - 130854
 */
public class PlayerCamera extends Node {
    
    private final BetterCharacterControl physicsCharacter;
    private final AnimControl animationControl;
    private final AnimChannel animationChannel;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 0);
    private float airTime;

    public PlayerCamera(String name,AssetManager assetManager, BulletAppState bulletAppState, Camera cam) {
        super(name);
        
    
  
        Node oto = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        oto.setLocalTranslation(0, 5, 0);
        scale(0.25f);
        setLocalTranslation(0, 2, 0);
        attachChild(oto);
        
        
        physicsCharacter = new BetterCharacterControl(1, 2.5f, 16f);
        addControl(physicsCharacter);
        
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        
        animationControl = oto.getControl(AnimControl.class);
        animationChannel = animationControl.createChannel();

        
        CameraNode camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 20,-50));
        camNode.lookAt(this.getLocalTranslation(), Vector3f.UNIT_Y);
        
        
        this.attachChild(camNode);


   }

    
    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(Vector3f viewDirection) {
        this.viewDirection = viewDirection;
    }
    

    
    void upDateAnimationPlayer() {
   
        if (walkDirection.length() == 0) {
            if (!"stand".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("stand", 1f);
            }
        } else {
            if (airTime > .3f) {
                if (!"stand".equals(animationChannel.getAnimationName())) {
                    animationChannel.setAnim("stand");
                }
            } else if (!"Walk".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("Walk", 10f);
            }
        }
    }

    void upDateKeys(float tpf, boolean left, boolean right, PlayerCamera player)
    {        
        Vector3f camDir  = getWorldRotation().mult(Vector3f.UNIT_Z);
       
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 100);
       
        if (left) {
            if(player.getLocalTranslation().x < 3)
                walkDirection.set(25, 0, walkDirection.z);
        } else if (right) {
            if(player.getLocalTranslation().x > -3)
                walkDirection.set(-25, 0, walkDirection.z);
        }
        
        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
 
        upDateAnimationPlayer();
    }
    
    
}
