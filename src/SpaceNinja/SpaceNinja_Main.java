package SpaceNinja;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

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
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;


/**
 * This is the SpaceNinja_Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author Daniele
 */
public class SpaceNinja_Main extends SimpleApplication implements PhysicsCollisionListener, ActionListener  {

    static SpaceNinja_Main app;
    
    private BulletAppState bulletAppState;
    private MyPlayer player;
    private boolean left = false, right = false, shot = false;
    private Node enemies = new Node("Inimigos");
    private int vida = 30, pontos = 0, aux = 0, auxE2 = 0, auxE3 = 0;
    private BitmapText live, points, gameOver;
    private boolean freeze;
    private AudioNode explosion, gameSound, soundShot, gameOverSound, wonSound;
    
    public static void main(String[] args) {
        
        //define inicialização, resolução de tela e inicia app
        AppSettings sett = new AppSettings(true);
        sett.setResolution(800, 600);  
        app = new SpaceNinja_Main();
        app.showSettings = false;
        app.setSettings(sett);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        createPhysics();
        createRoof();
        createGround();
        createWalls();
        createLight();
        
        createPlayer();
        createKeyboard();   
        
        createScore();
        createSound();
        
        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    private void criaInimigos() {
        float posicaox;

        //instancia os inimigos
        if(player.getLocalTranslation().z < 10000){
            if(auxE2 != 6){
                //seta posição aleatória
                posicaox = (float) (Math.random()* 16 - 8);
                //proporções do cubo
                Box mesh = new Box(1f, 1f, 1f);
                //define o nome do cubo
                Geometry geo = new Geometry("enemy1", mesh);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                //define a textura do cubo
                mat.setTexture("ColorMap", assetManager.loadTexture("Textures/enemy1.jpg"));
                geo.setMaterial(mat);
                //cria corpo rígido ao cubo
                RigidBodyControl corpoRigido = new RigidBodyControl(0.1f);
                geo.addControl(corpoRigido);
                geo.setLocalTranslation(posicaox, 7f, player.getLocalTranslation().z + 100f);
                corpoRigido.setPhysicsLocation(geo.getLocalTranslation());
                bulletAppState.getPhysicsSpace().add(geo);
                enemies.attachChild(geo);
                auxE2++;
            }
            else{
                posicaox = (float) (Math.random() * 16 - 8);
                Box mesh = new Box(1f, 1f, 1f);
                Geometry geo = new Geometry("enemy2", mesh);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture("Textures/enemy2.jpg"));
                geo.setMaterial(mat);
                RigidBodyControl corpoRigido = new RigidBodyControl(0.1f);
                geo.addControl(corpoRigido);
                geo.setLocalTranslation(posicaox, 7f, player.getLocalTranslation().z + 100f);
                corpoRigido.setPhysicsLocation(geo.getLocalTranslation());
                bulletAppState.getPhysicsSpace().add(geo);
                enemies.attachChild(geo);
                auxE2 = 0;
                auxE3++;
            }
            if(auxE3==3)
            {
                posicaox = (float) (Math.random() * 16 - 8);
                Box mesh = new Box(1f, 1f, 1f);
                Geometry geo = new Geometry("enemy3", mesh);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture("Textures/enemy3.jpg"));
                geo.setMaterial(mat);
                RigidBodyControl corpoRigido = new RigidBodyControl(0.1f);
                geo.addControl(corpoRigido);
                geo.setLocalTranslation(posicaox, 7f, player.getLocalTranslation().z + 150f);
                corpoRigido.setPhysicsLocation(geo.getLocalTranslation());
                bulletAppState.getPhysicsSpace().add(geo);
                enemies.attachChild(geo);
                auxE3 = 0;   
            }    
        }        

        rootNode.attachChild(enemies);
    }


    private void createPhysics(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    private void createKeyboard() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_Q));
        
        inputManager.addListener(this, "Left", "Right", "Space", "Start");
    }
    
    
     private void createPlayer() {
        player = new MyPlayer("player", assetManager, bulletAppState, cam);
        rootNode.attachChild(player);
        flyCam.setEnabled(true);
    }
    
    private void createGround(){
        Box boxMesh = new Box(16f,10f,8000f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture myTexture = assetManager.loadTexture("Textures/ambient.jpg");
        boxMat.setTexture("ColorMap", myTexture);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(0f, 0f, 7990f);
        rootNode.attachChild(boxGeo);
        
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(boxGeo);
        RigidBodyControl RigidBody = new RigidBodyControl(sceneShape, 0);
        boxGeo.addControl(RigidBody);

        bulletAppState.getPhysicsSpace().add(RigidBody);         
    }
    
    private void createRoof(){
        Box boxMesh = new Box(100f,0.1f,4000f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture myTexture = assetManager.loadTexture("Textures/ambient.jpg");
        boxMat.setTexture("ColorMap", myTexture);
        boxGeo.setMaterial(boxMat);
        //boxGeo.setLocalTranslation(10f, 7.5f, 3990f);
        rootNode.attachChild(boxGeo);

        RigidBodyControl RigidBody = new RigidBodyControl(0);
        boxGeo.addControl(RigidBody);

        bulletAppState.getPhysicsSpace().add(RigidBody);   

 
    }
    
    private void createWalls(){
        Box boxMesh = new Box(1f, 7f , 8000f);
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture myTexture = assetManager.loadTexture("Textures/ambient.jpg");
        boxMat.setTexture("ColorMap", myTexture);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(10f, 2.5f, 7990f);

        rootNode.attachChild(boxGeo);
        
        RigidBodyControl RigidBody = new RigidBodyControl(0);
        boxGeo.addControl(RigidBody);
        
        RigidBody.setPhysicsLocation(boxGeo.getLocalTranslation());

        bulletAppState.getPhysicsSpace().add(RigidBody);
        
        Box boxMesh2 = new Box(1f, 7f , 8000f);
        Geometry boxGeo2 = new Geometry("Colored Box", boxMesh2); 
         Material boxMat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture myTexture2 = assetManager.loadTexture("Textures/ambient.jpg");
        boxMat2.setTexture("ColorMap", myTexture2);
        boxGeo2.setMaterial(boxMat2); 
        boxGeo2.setLocalTranslation(-10f, 2.5f, 7990f);
        rootNode.attachChild(boxGeo2);
        
        RigidBodyControl RigidBody2 = new RigidBodyControl(0);        
        boxGeo2.addControl(RigidBody2);
        
        RigidBody2.setPhysicsLocation(boxGeo2.getLocalTranslation());

        bulletAppState.getPhysicsSpace().add(RigidBody2);
        
    }
    
    private void createLight() {

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
            aux++;
        
            //controla a quantidade de inimigos que é instanciada na tela
            if(aux > 250){
                criaInimigos();
                aux = 0;
            }

            live.setText("Vida = " + String.valueOf(vida));
            points.setText("Pontos = " + String.valueOf(pontos));


            for(Spatial d : enemies.getChildren())
                d.rotate(0, tpf, 0);

            if(player.getLocalTranslation().z >= 5000f || pontos >= 1200){
                gameOver.setText("Parabéns, você venceu!Aperte Q para uma nova partida.");
                gameOver.setSize(30);
                gameOver.setLocalTranslation(40, settings.getHeight()*0.6f, 0);
                guiNode.attachChild(gameOver);
                gameSound.stop();
                wonSound.play();
                player.gameOver = 2;
                //bulletAppState.wait();
                freeze = true;
                
                bulletAppState.setEnabled(false);   
            }

            if(vida <= 0){
                vida=0;
                gameOver.setText("Você perdeu :'( Aperte Q para jogar novamente!!");
                gameOver.setSize(35);
                gameOver.setLocalTranslation(50, settings.getHeight()*0.6f, 0);
                guiNode.attachChild(gameOver);
                player.gameOver = 1;
                gameSound.stop();
                gameOverSound.play();
                freeze = true;
                bulletAppState.setEnabled(false); 
                
            }
        }
        else
        {
            player.upDateAnimationPlayer();
            bulletAppState.setEnabled(false); 
        }
        
    }
    
     protected void createScore() {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        live = new BitmapText(guiFont, false);
        live.setSize(guiFont.getCharSet().getRenderedSize());        
        live.setColor(ColorRGBA.Red);
        live.setSize(30);
        live.setLocalTranslation(settings.getWidth()-250, settings.getHeight()*0.1f, 0);
        guiNode.attachChild(live);
        
        points = new BitmapText(guiFont, false);
        points.setSize(guiFont.getCharSet().getRenderedSize());        
        points.setColor(ColorRGBA.White);
        points.setSize(30);
        points.setLocalTranslation(settings.getWidth()-250, settings.getHeight()*0.9f , 0);
        guiNode.attachChild(points);

        gameOver = new BitmapText(guiFont, false);
        gameOver.setSize(guiFont.getCharSet().getRenderedSize());
        gameOver.setColor(ColorRGBA.Blue);

        gameOver.setLocalTranslation((settings.getWidth() / 2) - (guiFont.getCharSet().getRenderedSize() * (gameOver.getText().length() / 2 + 13)),
                settings.getHeight() + gameOver.getLineHeight() / 2 - 100, 0);
    }


    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    
    //Método que trata as colisões do jogo
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        Spatial nodeA = event.getNodeA();
        Spatial nodeB = event.getNodeB();
              
        if(nodeA.getName().equals("player"))
        {
            switch (nodeB.getName()) {
                case "enemy1":
                    if(shot)
                    {
                        pontos += 50;
                        shot = false;
                        soundShot.playInstance();
                        vida+=1;
                        explosion.stop();
                    }
                    else
                    {
                        vida -= 1;
                        explosion.play();
                    }
                    rootNode.detachChild(nodeB);
                    enemies.detachChild(nodeB);
                    bulletAppState.getPhysicsSpace().remove(nodeB);
                    explosao(nodeB.getLocalTranslation());
                    break;
                case "enemy2":
                     if(shot)
                    {
                        pontos += 100;
                        shot = false;
                        soundShot.playInstance();
                        vida+=2;
                        explosion.stop();
                    }
                    else
                    {
                    vida -= 2;
                    explosion.play();
                    }
                    rootNode.detachChild(nodeB);
                    enemies.detachChild(nodeB);
                    bulletAppState.getPhysicsSpace().remove(nodeB);
                    explosao(nodeB.getLocalTranslation());
                    break;
                case "enemy3":
                     if(shot)
                    {
                        pontos += 150;
                        shot = false;
                        soundShot.playInstance();
                        vida+=3;
                        explosion.stop();
                    }
                    else
                    {
                    vida -= 3;
                    explosion.playInstance();
                    }
                    rootNode.detachChild(nodeB);
                    enemies.detachChild(nodeB);
                    bulletAppState.getPhysicsSpace().remove(nodeB);
                    explosao(nodeB.getLocalTranslation());
                    break;
                default:
                    break;
            }
        }
        else if(nodeB.getName().equals("player")){
            switch (nodeA.getName()) {
                case "enemy1":
                     if(shot)
                    {
                        pontos += 50;
                        shot = false;
                        soundShot.playInstance();
                        vida+=1;
                    }
                    else
                    {
                        vida -= 1;
                        explosion.playInstance();
                    }
                    rootNode.detachChild(nodeA);
                    enemies.detachChild(nodeA);
                    bulletAppState.getPhysicsSpace().remove(nodeA);
                    explosao(nodeA.getLocalTranslation());
                    break;
                case "enemy2":
                     if(shot)
                    {
                        pontos += 100;
                        shot = false;
                        soundShot.playInstance();
                        vida+=2;
                    }
                    else
                    {
                        vida -= 2;
                        explosion.playInstance();
                    }
                    rootNode.detachChild(nodeA);
                    enemies.detachChild(nodeA);
                    bulletAppState.getPhysicsSpace().remove(nodeA);
                    explosao(nodeA.getLocalTranslation());
                    break;
                case "enemy3":
                     if(shot)
                    {
                        pontos += 150;
                        shot = false;
                        soundShot.playInstance();
                        vida+=3;
                    }
                    else
                    {
                        vida -= 3;
                        explosion.playInstance();
                    }
                    rootNode.detachChild(nodeA);
                    enemies.detachChild(nodeA);
                    bulletAppState.getPhysicsSpace().remove(nodeA);
                    explosao(nodeA.getLocalTranslation());
                    break;
                default:
                    break;
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
                shot = value;
                player.shot = value;
                break;
            case "Start":
                vida=30;
                pontos = 0;
                freeze=false;
                aux = 0;  
                gameOverSound.stop();
                gameSound.playInstance();
                bulletAppState.getPhysicsSpace().removeAll(enemies);
                bulletAppState.getPhysicsSpace().removeAll(player);
                rootNode.detachChild(player);
                rootNode.detachChild(enemies);  
                bulletAppState.setEnabled(true);
                guiNode.detachChild(gameOver);
                enemies = new Node();
                player.gameOver = 0;
                
                bulletAppState.setDebugEnabled(false);
                createPlayer();
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
    
    
    private void createSound(){
        explosion = new AudioNode(assetManager, "Sounds/explosion.wav", false);
        explosion.setLooping(false);
        explosion.setVolume(8);
        rootNode.attachChild(explosion);
        
        soundShot = new AudioNode(assetManager, "Sounds/shot.wav", false);
        soundShot.setLooping(false);
        soundShot.setVolume(6);
        rootNode.attachChild(soundShot);
        
        gameSound = new AudioNode(assetManager, "Sounds/marchOfTheResistance.wav", false);
        gameSound.setLooping(true);
        gameSound.setVolume(12);
        gameSound.play();
        rootNode.attachChild(gameSound);
        
        gameOverSound = new AudioNode(assetManager, "Sounds/gameOver.wav", false);
        gameOverSound.setLooping(false);
        gameOverSound.setVolume(8);
        rootNode.attachChild(gameOverSound);
        
        wonSound = new AudioNode(assetManager, "Sounds/wonSound.wav", false);
        wonSound.setLooping(false);
        wonSound.setVolume(8);
        rootNode.attachChild(wonSound);
        
    }
}
