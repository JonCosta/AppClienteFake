package br.ufpr.appclientefake;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private double latitude, longitude ;
	private String endereco, referencia ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Valores para teste
        endereco = "R. Mandirituba" ;
        referencia = "Esquina com Elvira Shaffer" ;
        latitude = -25.510754 ;
        longitude = -49.188846 ;
    }//Fecha onCreate

    public void onClick(View view){
    	switch(view.getId()){
    	case R.id.btnEnviar:
    		//Salva os dados na Intent e vai para a próxima
    		Intent it = new Intent(getApplicationContext(), BuscaActivity.class) ;
    		Bundle params = new Bundle() ;
    		params.putString("Endereco", endereco) ;
    		params.putString("Referencia", referencia) ;
    		params.putDouble("Latitude", latitude) ;
    		params.putDouble("Longitude", longitude) ;
    		it.putExtras(params) ;
    		startActivity(it) ;
    		break ;
    	}//Fecha switch
    }//Fecha onClick
    
    
}//Fecha Activity