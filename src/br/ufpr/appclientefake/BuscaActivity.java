package br.ufpr.appclientefake;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class BuscaActivity extends Activity {

	private double latitude, longitude ;
	private String endereco, referencia ;
	private Handler handler ;
	private int indice ;
	private boolean controle ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);
        handler = new Handler() ;
        controle = true ;
        getDados() ; //Obtém os dados do Pedido a partir da Intent anterior
        enviarPedido() ; //Envia o pedido à WS
    }//Fecha onCreate
    
    //Método que pega os dados da Intent anterior
    public void getDados(){
    	Intent it = getIntent() ;
    	if(it != null){
    		Bundle params = it.getExtras() ;
    		endereco = params.getString("Endereco") ;
    		referencia = params.getString("Referencia") ;
    		latitude = params.getDouble("Latitude") ;
    		longitude = params.getDouble("Longitude") ;
    	}
    }//Fecha getDados
    
    //Método que envia os dados à WS
    public void enviarPedido() {
    	HashMap params = new HashMap() ;
    	params.put("Endereco", endereco) ;
    	params.put("Referencia", referencia) ;
    	params.put("Latitude", latitude) ;
    	params.put("Longitude", longitude) ;
    	
    	JSONObject jsonParams = new JSONObject(params) ; //Cria o objeto JSON com as informações do HashMap
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWSenviarPedido), jsonParams) ; 
    	boolean enviado = false ;
    	indice = 0 ;
    	//Recebe os dados da resposta
    	try {
    		enviado = resp.getBoolean("Enviado") ; //Boolean para confirmar se dados foram enviados com sucesso
			indice = resp.getInt("Indice") ; //Pega índice do Pedido
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	if(enviado){
    		handler.post(run) ; //Se foram enviados os dados, inicia Runnable 
    	}
    }//Fecha enviarPedido
    
    public void enviarPedido(String placa, int indice) {
    	HashMap params = new HashMap() ;
    	params.put("Endereco", endereco) ;
    	params.put("Referencia", referencia) ;
    	params.put("Latitude", latitude) ;
    	params.put("Longitude", longitude) ;
    	params.put("Placa", placa) ;
    	params.put("Indice", indice) ;
    	
    	JSONObject jsonParams = new JSONObject(params) ; //Cria o objeto JSON com as informações do HashMap
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWSenviarNovoPedido), jsonParams) ; 
    	boolean enviado = false ;
    	indice = 0 ;
    	//Recebe os dados da resposta
    	try {
    		enviado = resp.getBoolean("Enviado") ; //Boolean para confirmar se dados foram enviados com sucesso
			indice = resp.getInt("Indice") ; //Pega índice do Pedido
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	if(enviado){
    		handler.post(run) ; //Se foram enviados os dados, inicia Runnable 
    	}else{
    		Toast.makeText(this, "Taxi não encontrado", Toast.LENGTH_SHORT).show() ;
    		finish() ;
    	}
    }//Fecha enviarPedido
    
    //Método da Runnable que acessa a WS para verificar se há Confirmação do Pedido enviado
    public void checarConfirmacao(){
    	HashMap params = new HashMap() ;
    	params.put("Indice", indice) ; //Usa o índice para pegar o mesmo Pedido do Array
    	JSONObject jsonParams = new JSONObject(params) ;
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWSchecarConfirmacao), jsonParams) ;
    	int achou = 0;
    	String nomeTaxista = "", placaTaxi = "" ;
    	try{
    		//Obtém-se todos os dados da resposta
    		achou = resp.getInt("Achou") ;
    		nomeTaxista = resp.getString("NomeTaxista") ;
    		placaTaxi = resp.getString("PlacaTaxi") ;
    	}catch(JSONException e){
    		e.printStackTrace() ;
    	}
    	//Verifica valor da variavel "confirm"
    	if(achou == 1){ // 1)O pedido foi aceito
    		Toast.makeText(this, nomeTaxista+" | "+placaTaxi, Toast.LENGTH_SHORT).show() ;
    		controle = false ; //Cancela o loop da Runnable
    	}else if(achou == 2){
    		enviarPedido(placaTaxi, indice) ;
    		Toast.makeText(this, "Pedido Recusado", Toast.LENGTH_SHORT).show() ;
    	}
	}//Fecha checarConfirmacao
    
    //Objeto Runnable que é executado continuamente
    Runnable run = new Runnable() {
		
		@Override
		public void run() {
			if(controle){
				checarConfirmacao() ;
				handler.postDelayed(this, 3000) ;
			}
		}//Fecha run
	};//Fecha Runnable
    
    @Override
    protected void onPause() {
    	super.onPause() ;    	
    	controle = false ;
    	finish() ;
    }//Fecha onPause
	
    @Override
    protected void onResume(){
    	super.onResume() ;
    	controle = true ;
    }
    
    @Override
    protected void onRestart(){
    	super.onRestart() ;
    	controle = true ;
    }
    
}//Fecha Activity