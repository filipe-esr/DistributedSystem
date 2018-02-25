/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package comInf;

import java.io.*;

/**
 *   Este tipo de dados define as mensagens que são trocadas entre os clientes e o servidor numa solução do Problema
 *   do Assaulto ao Museu que implementa o modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento
 *   estático dos threads barbeiro.
 *   A comunicação propriamente dita baseia-se na troca de objectos de tipo Message num canal TCP.
 */

public class Message implements Serializable
{
  /**
   *  Chave de serialização
   */
   private static final long serialVersionUID = 1001L;

  /* Tipos das mensagens */

  /**
   *  Inicialização do ficheiro de logging (operação pedida pelo cliente)
   */
   public static final int SETNFIC  =  1;

  /**
   *  Ficheiro de logging foi inicializado (resposta enviada pelo servidor)
   */
   public static final int NFICDONE =  2;
   
   /**
   *  Operação realizada com sucesso (resposta enviada pelo servidor)
   */
   public static final int ACK = 3;
   
   /**
   *  Pedido a distancia ao quarto (operação pedida pelo cliente)
   */
   public static final int REQDIST = 4;
   /**
   *  Resposta ao pedido da distancia ao quarto (resposta enviada pelo servidor)
   */
   public static final int RESPDIST = 5;
   
   /**
   *  Pedido para começar operações (operação pedida pelo cliente)
   */
   public static final int REQSTARTOP = 6;
   /**
   *  Resposta ao pedido para começar operações (resposta enviada pelo servidor)
   */
   public static final int RESPSTARTOP = 7;
   
   /**
   *  Pedido do sumário das operações (operação pedida pelo cliente)
   */
   public static final int REQSUMUPRESULTS = 8;
   /**
   *  Resposta ao pedido do sumário das operçãoes (resposta enviada pelo servidor)
   */
   public static final int RESPSUMUPRESULTS = 9;
   
   /**
   *  Pedido para takeARest (operação pedida pelo cliente)
   */
   public static final int REQTAKEREST = 10;
   /**
   *  Resposta ao pedido para takeARest (resposta enviada pelo servidor)
   */
   public static final int RESPTAKEREST = 11;
   
   /**
   *  Pedido para preparar AssaultParty (operação pedida pelo cliente)
   */
   public static final int REQPREPAREASSAULTPARTY = 12;
   
   /**
   *  Pedido para saber se PartyNeeded (operação pedida pelo cliente)
   */
   public static final int REQPARTYNEEDED = 13;
   /**
   *  Resposta ao pedido para saber se PartyNeeded (resposta enviada pelo servidor)
   */
   public static final int REQSENDASSAULTPARTY = 14;
   
   /**
   *  Pedido para saber se CollectedCanvas (operação pedida pelo cliente)
   */
   public static final int REQCOLLECTCANVAS = 15;
   /**
   *  Resposta ao pedido para saber se CollectedCanvas (resposta enviada pelo servidor)
   */
   public static final int RESPCOLLECTCANVAS = 16;
   
   /**
   *  Pedido para saber se parar Thieves (operação pedida pelo cliente)
   */
   public static final int REQSTOPT = 17;
   //   ^ MT FUNCIONS ^
   
   /**
   *  Pedido para HandACanvas (operação pedida pelo cliente)
   */
   public static final int REQHANDACANVAS = 18;
   /**
   *  Resposta ao pedido para HandACanvas (resposta enviada pelo servidor)
   */
   public static final int RESPHANDACANVAS = 19;

   /**
   *  Pedido para AmINeeded (operação pedida pelo cliente)
   */
   public static final int REQAMINEEDED = 20;
   /**
   *  Resposta ao pedido para AmINeeded (resposta enviada pelo servidor)
   */
   public static final int RESPAMINEEDED = 21;
   
   /**
   *  Pedido para AmINeeded (operação pedida pelo cliente)
   */
   public static final int REQPREPAREEXCURSION = 22;
   /**
   *  Resposta ao pedido para AmINeeded (resposta enviada pelo servidor)
   */
   public static final int RESPPREPAREEXCURSION = 23;
   
   /**
   *  Pedido para CrawlIn (operação pedida pelo cliente)
   */
   public static final int REQCRAWLIN = 24;
   /**
   *  Resposta ao pedido para CrawlIn (resposta enviada pelo servidor)
   */
   public static final int RESPCRAWLIN = 25;
   
   /**
   *  Pedido para RollACanvas (operação pedida pelo cliente)
   */
   public static final int REQROLLACANVAS = 26;
   /**
   *  Resposta ao pedido para RollACanvas (resposta enviada pelo servidor)
   */
   public static final int RESPROLLACANVAS = 27;
   
   /**
   *  Pedido para ReverseDirection (operação pedida pelo cliente)
   */
   public static final int REQREVERSEDIRECTION = 28;
   /**
   *  Resposta ao pedido para ReverseDirection (resposta enviada pelo servidor)
   */
   public static final int RESPREVERSEDIRECTION = 29;
   
   /**
   *  Pedido para CrawlOut (operação pedida pelo cliente)
   */
   public static final int REQCRAWLOUT = 30;
   /**
   *  Resposta ao pedido para CrawlOut (resposta enviada pelo servidor)
   */
   public static final int RESPCRAWLOUT = 31;
   // ^ T FUNCIONS ^
   
   
   /**
   *  Pedido para PrintFirst (operação pedida pelo cliente)
   */
   public static final int REQPRINTFIRST = 32;
   
   /**
   *  Pedido para PrintChange (operação pedida pelo cliente)
   */
   public static final int REQPRINTCHANGE = 33;
   
   /**
   *  Pedido para PrintAll (operação pedida pelo cliente)
   */
   public static final int REQPRINTALL = 34;
   
   /**
   *  Pedido para SetMTState (operação pedida pelo cliente)
   */
   public static final int REQSETMTSTATE = 35;
           
   /**
   *  Pedido para SetTSituation (operação pedida pelo cliente)
   */
   public static final int REQSETTSITUATION = 36;
    
   /**
   *  Pedido para SetTMaxDisp (operação pedida pelo cliente)
   */
   public static final int REQSETTMAXDISP = 37;
    
   /**
   *  Pedido para SetTState (operação pedida pelo cliente)
   */
   public static final int REQSETTSTATE = 38;
    
   /**
   *  Pedido para SetTCanvasStatus (operação pedida pelo cliente)
   */
   public static final int REQSETTCANVASSTATUS = 39;
    
   /**
   *  Pedido para SetThiefPos (operação pedida pelo cliente)
   */
   public static final int REQSETTHIEFPOS = 40;
    
   /**
   *  Pedido para AddCanvasTotal (operação pedida pelo cliente)
   */
   public static final int REQADDCANVASTOTAL = 41;
    
   /**
   *  Pedido para RemoveCanvasFromRoom (operação pedida pelo cliente)
   */
   public static final int REQREMOVECANVASFROMROOM = 42;
    
   /**
   *  Pedido para SetMuseumRooms (operação pedida pelo cliente)
   */
   public static final int REQSETMUSEUMROOMS = 43;
   
   /**
   *  Pedido para SetPartyRoom (operação pedida pelo cliente)
   */
   public static final int REQSETPARTYROOM = 44;
    // ^ LOGS ^
   
   /**
   *  Termino do ciclo de vida (resposta enviada pelo servidor)
   */
   public static final int END = 45;


  /* Campos das mensagens */

   /**
   *  Tipo da mensagem
   */
   private int msgType = -1;

   /**
   *  Identificação do quarto
   */
   private int room = -1;

   /**
   *  Identificação do roomDist (distância ao quarto)
   */
   private int roomDist = -1;

   /**
   *  Identificação da distância até cada quarto
   */
   private int[] distRooms = null;
   
   /**
   *  Identificação da partyFree (grupo livre)
   */
   private int partyFree = -1;
   
   /**
   *  Identificação da partyIsNeeded (grupo é chamado)
   */
   private boolean partyIsNeeded = false;
   
   /**
   *  Identificação do collectCanvasValue
   */
   private boolean collectCanvasValue = true;
   
   /**
   *  Identificação do thiefID (ID do ladrão)
   */
   private int thiefID = -1;
   
   /**
   *  Identificação do maxDisplacement
   */
   private int maxDisplacement = -1;
   
   /**
   *  Identificação do canvasCollected
   */
   private boolean canvasCollected = false;
   
   /**
   *  Identificação da thiefParty (grupo do ladrão)
   */
   private int thiefParty = -1;
   
   /**
   *  Identificação da rolledCanvas
   */
   private boolean rolledCanvas = false;
   
   /**
   *  Identificação se isNeeded
   */
   private boolean isNeeded = false;
   
   /**
   *  Identificação do thiefState
   */
   private String thiefState = null;
   
   /**
   *  Identificação do thiefStateLog
   */
   private int thiefStateLog = -1;
   
   /**
   *  Identificação da situation (do ladrão, em grupo ou à espera)
   */
   private char situation = 'x';
   
   /**
   *  Identificação do mtState
   */
   private String mtState = null;
   
   /**
   *  Identificação do mtStateLog
   */
   private int mtStateLog = -1;
   
   /**
   *  Identificação do canvasStatusBitLog
   */
   private int canvasStatusBitLog = -1;
   
   /**
   *  Identificação do movementLog
   */
   private int movementLog = -1;
   
   /**
   *  Identificação do canvasRoom
   */
   private int canvasRoom = -1;
   
   /**
   *  Identificação do array canvasRooms (quadro em cada quarto)
   */
   private int[] canvasRooms = null;

  /**
   *  Instanciação de uma mensagem (forma 1).
   *
   *    @param type tipo da mensagem
   */

   public Message (int type)
   {
      msgType = type;
   }

   /**
    * Instanciação de uma mensagem (forma 2).
    * 
    * @param type tipo da mensagem
    * @param value
    */
   public Message (int type, int value)
   {
       msgType = type;
       switch(type){
           case REQSETMTSTATE:
               this.mtStateLog = value;
               break;
           case RESPTAKEREST:
               this.partyFree = value;
               break;
           case REQAMINEEDED:
               this.thiefParty = value;
               break;
           case REQPREPAREEXCURSION:
               this.thiefID = value;
               break;
           case RESPPREPAREEXCURSION:
               this.room = value;
               break;
           case REQROLLACANVAS:
               this.room = value;
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 3).
    * 
    * @param type tipo da mensagem
    * @param value
    */
   public Message (int type, int[] value)
   {
       msgType = type;
       switch(type){
           case RESPDIST:
               this.distRooms = value;
               break;
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 4).
    * 
    * @param type tipo da mensagem
    * @param value value
    */
   public Message (int type, boolean value)
   {
       msgType = type;
       switch(type){
           case RESPCOLLECTCANVAS:
               this.collectCanvasValue = value;
               break;
           case RESPHANDACANVAS:
               this.rolledCanvas = value;
               break;
           case RESPAMINEEDED:
               this.isNeeded = value;
               break;
           case RESPROLLACANVAS:
               this.canvasCollected = value;
               break;
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 5).
    * 
    * @param type tipo da mensagem
    * @param value value
    */
   public Message (int type, String value)
   {
       msgType = type;
       switch(type){
           case RESPSTARTOP:
               this.mtState = value;
               break;
           case RESPSUMUPRESULTS:
               this.mtState = value;
               break;
           case RESPCRAWLIN:
               this.thiefState = value;
               break;
           case RESPREVERSEDIRECTION:
               this.thiefState = value;
           case RESPCRAWLOUT:
               this.thiefState = value;
               break;
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 6).
    * 
    * @param type tipo da mensagem
    * @param value1 value
    * @param value2 value
    */
   public Message (int type, int value1, int value2)
   {
       msgType = type;
       switch(type){
           case REQSETPARTYROOM:
               this.partyFree = value1;
               this.room = value2;
               break;
           case REQSETTMAXDISP:
               this.thiefID = value1;
               this.maxDisplacement = value2;
               break;
           case REQCRAWLIN:
               this.thiefID = value1;
               this.maxDisplacement = value2;
               break;
           case REQSETTSTATE:
               this.thiefID = value1;
               this.thiefStateLog = value2;
           case REQSETTCANVASSTATUS:
               this.thiefID = value1;
               this.canvasStatusBitLog = value2;
           case REQCRAWLOUT:
               this.thiefID = value1;
               this.maxDisplacement = value2;
               break;
           case REQSETTHIEFPOS:
               this.thiefID = value1;
               this.movementLog = value2;
           case REQREMOVECANVASFROMROOM:
               this.room = value1;
               this.canvasRoom = value2;
           case REQPREPAREASSAULTPARTY:
               this.room = value1;
               this.roomDist = value2;
               break;     
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 7).
    * 
    * @param type tipo da mensagem
    * @param value1 value
    * @param value2 value
    */
   public Message (int type, int[] value1, int[] value2)
   {
       msgType = type;
       switch(type){
           case REQSETMUSEUMROOMS:
               this.canvasRooms = value1;
               this.distRooms = value2;
               break;
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 8).
    * 
    * @param type tipo da mensagem
    * @param value1 value
    * @param value2 value
    */
   public Message (int type, int value1, boolean value2)
   {
       msgType = type;
       switch(type){
           case REQPARTYNEEDED:
               this.partyFree = value1;
               this.partyIsNeeded = value2;
               break;
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 9).
    * 
    * @param type tipo da mensagem
    * @param value1 value
    * @param value2 value
    * @param value3 value
    */
   public Message (int type, int value1, int value2, char value3)
   {
       msgType = type;
       switch(type){
           case REQSETTSITUATION:
               this.thiefID = value1;
               this.thiefStateLog = value2;
               this.situation = value3;
               break;
       }
   }
   
   /**
    * Instanciação de uma mensagem (forma 10).
    * 
    * @param type tipo da mensagem
    * @param value1 value
    * @param value2 value
    * @param value3 value
    */
   public Message (int type, boolean value1, int value2, int value3)
   {
       msgType = type;
       switch(type){
           case REQHANDACANVAS:
               this.canvasCollected = value1;
               this.thiefID = value2;
               this.thiefParty = value3;
               break;
       }
   }

   /**
   *  Obtenção do valor do campo tipo da mensagem.
   *
   *    @return tipo da mensagem
   */
   public int getType ()
   {
      return (msgType);
   }
   
   /**
   *  Obtenção do valor do quarto.
   *
   *    @return tipo da mensagem
   */
   public int getRoom (){
       return (room);
   }

   /**
   *  Obtenção do valor da distância ao quarto.
   *
   *    @return tipo da mensagem
   */
   public int getRoomDist (){
       return (roomDist);
   }

   /**
   *  Obtenção do array com valores das distâncias aos quartos.
   *
   *    @return tipo da mensagem
   */
   public int[] getDistRooms (){
       return (distRooms);
   }
   
   /**
   *  Obtenção do valor se o grupo está livre.
   *
   *    @return tipo da mensagem
   */
   public int getPartyFree (){
       return (partyFree);
   }
   
   /**
   *  Obtenção do valor se o grupo é chamado.
   *
   *    @return tipo da mensagem
   */
   public boolean getPartyIsNeeded (){
       return (partyIsNeeded);
   }
   
   /**
   *  Obtenção do valor se tem quadro colectado.
   *
   *    @return tipo da mensagem
   */
   public boolean getCollectCanvasValue (){
       return (collectCanvasValue);
   }
   
   /**
   *  Obtenção do valor id do ladrão.
   *
   *    @return tipo da mensagem
   */
   public int getThiefID (){
       return (thiefID);
   }
   
   /**
   *  Obtenção do valor de separação máxima entre elementos do grupo.
   *
   *    @return tipo da mensagem
   */
   public int getMaxDisplacement (){
       return (maxDisplacement);
   }
   
   /**
   *  Obtenção do valor quadro colectado.
   *
   *    @return tipo da mensagem
   */
   public boolean getCanvasCollected (){
       return (canvasCollected);
   }
   
   /**
   *  Obtenção do valor grupo de ladrões.
   *
   *    @return tipo da mensagem
   */
   public int getThiefParty (){
       return (thiefParty);
   }
   
   /**
   *  Obtenção do valor quadro obtido.
   *
   *    @return tipo da mensagem
   */
   public boolean getRolledCanvas (){
       return (rolledCanvas);
   }
   
   /**
   *  Obtenção do valor se o ladrão é chamado.
   *
   *    @return tipo da mensagem
   */
   public boolean getIsNeeded (){
       return (isNeeded);
   }
   
   /**
   *  Obtenção do valor de estado do ladrão.
   *
   *    @return tipo da mensagem
   */
   public String getThiefState (){
       return (thiefState);
   }
   
   /**
   *  Obtenção do valor do estado do ladrão no log.
   *
   *    @return tipo da mensagem
   */
   public int getThiefStateLog (){
       return (thiefStateLog);
   }
   
   /**
   *  Obtenção do valor da situação do ladrão (ocupado ou livre).
   *
   *    @return tipo da mensagem
   */
   public char getSituation (){
       return (situation);
   }
   
   /**
   *  Obtenção do valor de estado do ladrão chefe.
   *
   *    @return tipo da mensagem
   */
   public String getMTState (){
       return (mtState);
   }
   
   /**
   *  Obtenção do valor de estado do ladrão chefe no log.
   *
   *    @return tipo da mensagem
   */
   public int getMTStateLog (){
       return (mtStateLog);
   }
   
   /**
   *  Obtenção do valor do estado do canvas no log.
   *
   *    @return tipo da mensagem
   */
   public int getCanvasStatusBitLog (){
       return (canvasStatusBitLog);
   }
   
   /**
   *  Obtenção do valor do movimento no log.
   *
   *    @return tipo da mensagem
   */
   public int getMovementLog (){
       return (movementLog);
   }
   
   /**
   *  Obtenção do valor de canvas no quarto.
   *
   *    @return tipo da mensagem
   */
   public int getCanvasRoom (){
       return (canvasRoom);
   }
   
   /**
   *  Obtenção do array de valores de canvas nos quartos.
   *
   *    @return tipo da mensagem
   */
   public int[] getCanvasRooms (){
       return (canvasRooms);
   }
   
   /**
   *  Impressão dos campos internos.
   *  Usada para debugging.
   *
   *    @return string contendo, em linhas separadas, a concatenação da identificação de cada campo e valor respectivo
   */
   @Override
   public String toString ()
   {
      return ("Tipo = " + msgType +
              "\nNumber of the room = " + room +
              "\nDistance To Room = " + roomDist +
              "\nRooms Distance Array = " + distRooms +
              "\nNumber PartyFree = " + partyFree +
              "\nPartyIsNeeded = " + partyIsNeeded +
              "\nCanvas Collected = " + collectCanvasValue +
              "\nThief ID = " + thiefID +
              "\nMaximum Displacement = " + maxDisplacement +
              "\nCanvas Collected = " + canvasCollected +
              "\nNumber Thief Party = " + thiefParty +
              "\nRolled Canvas = " + rolledCanvas +
              "\nIs Needed = " + isNeeded +
              "\nThief State = " + thiefState +
              "\nNumber Thief State = " + thiefStateLog +
              "\nSituation = " + situation +
              "\nMasterThief State = " + mtState +
              "\nNumber MasterThief State = " + mtStateLog +
              "\nCanvas Collected Status = " + canvasStatusBitLog +
              "\nMovement Value = " + movementLog +
              "\nCanvas Room = " + canvasRoom +
              "\nArray Canvas Room = " + canvasRooms);
   }
}
