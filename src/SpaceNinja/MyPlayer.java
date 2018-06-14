/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SpaceNinja;


import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;


/**
 *
 * @author Daniele
 */
public class MyPlayer extends Node {
    
    private final BetterCharacterControl physicsCharacter;
    private final AnimControl animationControl;
    private final AnimChannel animationChannel;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 0);
    private float airTime;
    public boolean shot;
    public int gameOver = 0;

    public MyPlayer(String name,AssetManager assetManager, BulletAppState bulletAppState, Camera cam) {
        super(name);
        
    
        //Cria o Player, define tamanho e localização e insere o nó
  
        Node player1 = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        player1.setLocalTranslation(0, 5, -30);
        player1.rotate(0, 600, 0);
        player1.scale(0.06f);
        scale(0.25f);
        setLocalTranslation(0, 2, 0);
        attachChild(player1);
        
        //Adiciona a física 
        physicsCharacter = new BetterCharacterControl(1.2f, 0.7f, 20f);
        addControl(physicsCharacter);
        
        
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        
        
        animationControl = player1.getControl(AnimControl.class);
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
            if (!"Idle1".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("Idle1", 1f);
            }
        } 
        else 
        {
            if (!"Walk".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("Walk", 3f);
            }
        }
        if(this.shot)
        {
            animationChannel.setAnim("Attack3",5f);
            this.shot = false;
            
        }   
        if(this.gameOver == 1)
        {
            animationChannel.setAnim("Backflip",3f);
            animationChannel.setLoopMode(LoopMode.DontLoop);
            animationChannel.setAnim("Death2",2f);
            animationChannel.setLoopMode(LoopMode.DontLoop);
        }
        else if(this.gameOver == 2)
        {
            animationChannel.setAnim("Climb",3f);
        }
        
    }

    void upDateKeys(float tpf, boolean left, boolean right, MyPlayer player)
    {        
        Vector3f camDir  = getWorldRotation().mult(Vector3f.UNIT_Z);
       
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 50);//determina a "velocidade" do player
       
        if (left) {
            if(player.getLocalTranslation().x < 7)
                walkDirection.set(25, 0, walkDirection.z);
        } 
        else if (right) {
            if(player.getLocalTranslation().x > -7)
                walkDirection.set(-25, 0, walkDirection.z);
        }
        
        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
 
        upDateAnimationPlayer();
    }
    
    
}
