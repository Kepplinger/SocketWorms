package gameobjects;

import java.io.Serializable;

/**
 * Created by Andreas on 20.06.2016.
 */
public enum UpdateInformation implements Serializable{
    World_a_Player, //Die Welt und die Spieler sollten gesendet werden
    World,  //Es werden keine Spieler gesendet
    Player  //Es wird die Welt nicht gesendet
}
