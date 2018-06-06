package JustRun_130854;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

/**
 * @author Giuliano Cesari Tacioli - 130854
 */

public class JustRunMain extends SimpleApplication implements PhysicsCollisionListener, ActionListener  {

    static JustRunMain app;
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1500, 900);        
        app = new JustRunMain();
        app.showSettings = false;
        app.setSettings(settings);
        app.start();
    }

    private BulletAppState bulletAppState;
    private PlayerCamera player;
    private boolean left = false, right = false;
    private Node enemies = new Node("Inimigos");
    private int vida = 1000;
    private int count = 0;
    private int countVida = 0;
    private BitmapText information;
    private BitmapText fimDoJogo;
    private boolean freeze;
    private AudioNode explosao, gameover, life;
    
    
    @Override
    public void simpleInitApp() {
        criaFisica();
        criaChao();
        criaParedes();
        criaTeto();
        criaLuz();
        criaPlayer();
        criaTeclado();        
        criaPlacar();
        criaSom();
        
        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    private void criaInimigos() {
        float posicaox;

        if(player.getLocalTranslation().z < 7900){
            if(countVida != 10){
                posicaox = (float) (Math.random() * 10 - 5);
                Box mesh = new Box(1f, 1f, 1f);
                Geometry geo = new Geometry("enemy", mesh);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture("Textures/bomb.jpg"));
                geo.setMaterial(mat);
                RigidBodyControl corpoRigido = new RigidBodyControl(0.1f);
                geo.addControl(corpoRigido);
                geo.setLocalTranslation(posicaox, 1.5f, player.getLocalTranslation().z + 100f);
                corpoRigido.setPhysicsLocation(geo.getLocalTranslation());
                bulletAppState.getPhysicsSpace().add(geo);
                enemies.attachChild(geo);
                countVida++;
            }
            else{
                posicaox = (float) (Math.random() * 10 - 5);
                Box mesh = new Box(1f, 1f, 1f);
                Geometry geo = new Geometry("life", mesh);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture("Textures/heart.jpg"));
                geo.setMaterial(mat);
                RigidBodyControl corpoRigido = new RigidBodyControl(0.1f);
                geo.addControl(corpoRigido);
                geo.setLocalTranslation(posicaox, 1.5f, player.getLocalTranslation().z + 100f);
                corpoRigido.setPhysicsLocation(geo.getLocalTranslation());
                bulletAppState.getPhysicsSpace().add(geo);
                enemies.attachChild(geo);
                countVida = 0;
            }
        }        

        rootNode.attachChild(enemies);
    }


    private void criaFisica(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    private void criaTeclado() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left", "Right", "Space");
    }
    
    
     private void criaPlayer() {
        player = new PlayerCamera("player", assetManager, bulletAppState, cam);
        rootNode.attachChild(player);
        flyCam.setEnabled(true);
    }
    
    private void criaChao(){
        Box boxMesh = new Box(7f,1f,4000f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/grama.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(0f, 0f, 3990f);
        rootNode.attachChild(boxGeo);
        
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(boxGeo);
        RigidBodyControl RigidBody = new RigidBodyControl(sceneShape, 0);
        boxGeo.addControl(RigidBody);

        bulletAppState.getPhysicsSpace().add(RigidBody);         
    }
    
    private void criaTeto(){
        Box boxMesh = new Box(100f,0.1f,4000f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/Pond.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(0, 7.5f, 3990f);
        rootNode.attachChild(boxGeo);

        RigidBodyControl RigidBody = new RigidBodyControl(0);
        boxGeo.addControl(RigidBody);

        bulletAppState.getPhysicsSpace().add(RigidBody);   

        
    }
    
    private void criaParedes(){
        Box boxMesh = new Box(1f, 2f , 4000f);
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMat.setBoolean("UseMaterialColors", true); 
        boxMat.setColor("Ambient", ColorRGBA.Blue); 
        boxMat.setColor("Diffuse", ColorRGBA.Blue); 
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(6f, 2.5f, 3990f);

        rootNode.attachChild(boxGeo);
        
        RigidBodyControl RigidBody = new RigidBodyControl(0);
        boxGeo.addControl(RigidBody);
        
        RigidBody.setPhysicsLocation(boxGeo.getLocalTranslation());

        bulletAppState.getPhysicsSpace().add(RigidBody);
        
        Box boxMesh2 = new Box(1f, 2f , 4000f);
        Geometry boxGeo2 = new Geometry("Colored Box", boxMesh2); 
        Material boxMat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMat2.setBoolean("UseMaterialColors", true); 
        boxMat2.setColor("Ambient", ColorRGBA.Blue); 
        boxMat2.setColor("Diffuse", ColorRGBA.Blue); 
        boxGeo2.setMaterial(boxMat2); 
        boxGeo2.setLocalTranslation(-6f, 2.5f, 3990f);
        rootNode.attachChild(boxGeo2);
        
        RigidBodyControl RigidBody2 = new RigidBodyControl(0);        
        boxGeo2.addControl(RigidBody2);
        
        RigidBody2.setPhysicsLocation(boxGeo2.getLocalTranslation());

        bulletAppState.getPhysicsSpace().add(RigidBody2);
        
    }
    
    private void criaLuz() {

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-10.5f, -15f, -10.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection((new Vector3f(10.5f, -15f, 10.5f)).normalizeLocal());
        sun2.setColor(ColorRGBA.White);
        rootNode.addLight(sun2);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
        if(!freeze){
            player.upDateKeys(tpf, left, right, player);
            count++;
        
        if(count > 150){
            criaInimigos();
            count = 0;
        }
        
        information.setText("Vida = " + String.valueOf(vida));

                
        for(Spatial d : enemies.getChildren())
            d.rotate(0, tpf, 0);
        
        if(player.getLocalTranslation().z >= 7985f){
            fimDoJogo.setText("Parabens, voce completou o jogo!!");
            fimDoJogo.setSize(30);
            guiNode.attachChild(fimDoJogo);
            freeze = true;
            bulletAppState.setEnabled(false);   
        }
  
        if(vida <= 0){
            fimDoJogo.setText("Você perdeu! Aperte ESPACO para continuar!!");
            fimDoJogo.setSize(30);
            guiNode.attachChild(fimDoJogo);
            freeze = true;
            bulletAppState.setEnabled(false);           
        }
        }
    }
    
    protected void criaPlacar() {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        information = new BitmapText(guiFont, false);
        information.setSize(guiFont.getCharSet().getRenderedSize());        
        information.setColor(ColorRGBA.Green);
        information.setSize(30);
        information.setLocalTranslation(0, settings.getHeight() - 70, 0);
        guiNode.attachChild(information);

        fimDoJogo = new BitmapText(guiFont, false);
        fimDoJogo.setSize(guiFont.getCharSet().getRenderedSize());

        fimDoJogo.setLocalTranslation((settings.getWidth() / 2) - (guiFont.getCharSet().getRenderedSize() * (fimDoJogo.getText().length() / 2 + 13)),
                settings.getHeight() + fimDoJogo.getLineHeight() / 2 - 100, 0);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        Spatial nodeA = event.getNodeA();
        Spatial nodeB = event.getNodeB();
              
        if(nodeA.getName().equals("player"))
        {
            if(nodeB.getName().equals("enemy")){
             vida -= 100;
             rootNode.detachChild(nodeB);
             enemies.detachChild(nodeB);
             bulletAppState.getPhysicsSpace().remove(nodeB);
             explosao(nodeB.getLocalTranslation());
             explosao.playInstance();
            }
        }
        else if(nodeB.getName().equals("player")){
            if(nodeA.getName().equals("enemy")){
                vida -= 100;
                rootNode.detachChild(nodeA);
                enemies.detachChild(nodeA);
                bulletAppState.getPhysicsSpace().remove(nodeA);
                explosao(nodeA.getLocalTranslation());
                explosao.playInstance();
            }
        }
        if(nodeA.getName().equals("player"))
        {
            if(nodeB.getName().equals("life")){
             vida += 100;
             rootNode.detachChild(nodeB);
             enemies.detachChild(nodeB);
             bulletAppState.getPhysicsSpace().remove(nodeB);
             explosao(nodeB.getLocalTranslation());
             life.playInstance();
            }
        }
        else if(nodeB.getName().equals("player")){
            if(nodeA.getName().equals("life")){
                vida += 100;
                rootNode.detachChild(nodeA);
                enemies.detachChild(nodeA);
                bulletAppState.getPhysicsSpace().remove(nodeA);
                explosao(nodeA.getLocalTranslation());
                life.playInstance();
            }
        }        
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        switch (name) {
            case "Left":
                left = value;
                break;
            case "Right":
                right = value;
                break;
            case "Space":
                vida=1000;
                freeze=false;
                count = 0;                
                bulletAppState.getPhysicsSpace().removeAll(enemies);
                bulletAppState.getPhysicsSpace().removeAll(player);
                rootNode.detachChild(player);
                rootNode.detachChild(enemies);  
                bulletAppState.setEnabled(true);
                guiNode.detachChild(fimDoJogo);
                enemies = new Node();
                
                bulletAppState.setDebugEnabled(true);
                criaPlayer();
                break;                
        }
    }
    
    private void explosao(Vector3f pos) {
        pos.y += 2;
        ParticleEmitter debrisEffect = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 100);
        debrisEffect.setLocalTranslation(pos);
        Material debrisMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        debrisMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/Debris.png"));
        debrisEffect.setMaterial(debrisMat);
        debrisEffect.setImagesX(3);
        debrisEffect.setImagesY(3); // 3x3 texture animation
        debrisEffect.setRotateSpeed(4);
        debrisEffect.setSelectRandomImage(true);
        debrisEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        debrisEffect.setStartColor(new ColorRGBA(1f, 1f, 1f, 1f));
        debrisEffect.setGravity(0f, 6f, 0f);
        debrisEffect.getParticleInfluencer().setVelocityVariation(.60f);
        debrisEffect.setHighLife(3000);
        rootNode.attachChild(debrisEffect);
        debrisEffect.emitAllParticles();
    }
    
    
    private void criaSom(){
        explosao = new AudioNode(assetManager, "Sounds/Explosion.wav", false);
        explosao.setLooping(false);
        explosao.setVolume(2);
        rootNode.attachChild(explosao);
        
        life = new AudioNode(assetManager, "Sounds/lifeSound.wav", false);
        life.setLooping(false);
        life.setVolume(2);
        rootNode.attachChild(explosao);
        
    }
}
