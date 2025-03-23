package com.example.memorygame

import android.app.Activity
import android.icu.text.AlphabeticIndex
import android.os.Bundle
import android.os.Handler
import android.service.quickaccesswallet.WalletCard
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memorygame.databinding.ActivityMainBinding
import com.example.memorygame.databinding.ImagesBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var imagesBinding: ImagesBinding

    //Inicializamos un arreglo que contendra las cartas
    private lateinit var cardImages: Array<ImageView>

    //Arreglo que contiene los identificadores de las cartas
    private var cartasId= arrayOf(101,102,103,104,105,106,201,202,203,204,205,206)

    //Arreglo con las imagenes de los animales
    private val images= intArrayOf(R.drawable.jirafa101, R.drawable.elefante102, R.drawable.tigre103, R.drawable.leon104, R.drawable.rino105, R.drawable.bambi106,
        R.drawable.jirafa201, R.drawable.elefante202, R.drawable.tigre203, R.drawable.leon204, R.drawable.rino205, R.drawable.bambi206)

    //Creamos variables para manejar cartas seleccionadas
    //Variable que almacenara la primera carta seleccionada
    private var primeraCarta= -1
    //Variable que almacenara la segunda carta seleccionada
    private var segundaCarta=-1

    //Indice de la primera carta seleccionada
    private var clickPrimero= -1
    //Indice d la segunda carta seleccionada
    private var clickSegundo= -1

    //Variable para controlar los turnos y puntajes
    //Se inicializa el turno para primer jugador
    private var turno = 1
    //Puntos del jugador
    private var puntosP1 = 0
    //Puntos de la CPU
    private var puntosP2=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializa el binding y establece la vista
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializar el binding de las imagenes
        imagesBinding= ImagesBinding.bind(binding.includeImages.root)


        //Accedemos a los elementos con binding
        binding.tvP1.text="Jugador 1: 0 Pts"
        binding.tvP2.text="Jugador 2: 0 Pts"

        //  Inicializamos el array de ImageView asociando la carta con su referencia
        cardImages= arrayOf(

            imagesBinding.iv11, imagesBinding.iv12, imagesBinding.iv13, imagesBinding.iv14,
            imagesBinding.iv21, imagesBinding.iv22, imagesBinding.iv23, imagesBinding.iv24,
            imagesBinding.iv31, imagesBinding.iv32, imagesBinding.iv33, imagesBinding.iv34
        )

        //Asignar los eventos de click para las cartas
        for(i in cardImages.indices) {
            //Guardar el indice de la carta en el tag
            cardImages[i].tag = i
            //Asignar el evento
            cardImages[i].setOnClickListener {
                cardClicked(it)
            }
        }

        //Mezclar las cartas
        cartasId.shuffle()

        //Configuracion del boton de reinicio
        binding.btnRestart.setOnClickListener{restartGame()}
    }

    //Metodo para cuando se clickea una carta
    private fun cardClicked(view: View){
        //Se obtiene el indice de la carta seleccionada
        val selectIndice= view.tag.toString().toInt()
        //Llamar a la funcion para mostrar la carta
        showCard(view as ImageView, selectIndice)
    }

    //Metodo para mostrar la carta
    private fun showCard(iv: ImageView, cardIndex: Int){
        //Obtener la imagen de la carta
        val cardImage= getcardImage(cartasId[cardIndex])
        //Asigna la imagen a la ImageView
        iv.setImageResource(cardImage)

        //Condicion si es la primera carta seleccionada
        if(primeraCarta == -1){
            primeraCarta= cartasId[cardIndex]
            clickPrimero=cardIndex
        }else{
            segundaCarta= cartasId[cardIndex]
            clickSegundo=cardIndex

            //Deshabilita interaccion para validar
            cardImages.forEach { it.isEnabled= false }

            //Esperar un segundo y luego verificar si son las cartas coincidentes
            Handler().postDelayed({ checkMatch()}, 1000)
        }
    }

    //Metodo para obtener la imagen correspondiente
    private fun getcardImage(card: Int): Int{
        //Buscar la posicion en el array de cartas
        val index= cartasId.indexOf(card)
        return images[index]
    }

    //Metodo para comprobar si dos cartas seleccionadas coinciden
    private fun checkMatch(){
        val iv1= cardImages[clickPrimero]
        val iv2= cardImages[clickSegundo]

        if (primeraCarta%100 == segundaCarta%100){
            if(turno == 1) {
                puntosP1++
                binding.tvP1.text= "Jugador 1: $puntosP1 Pts"
            }else{
               puntosP2 ++
               binding.tvP2.text= "Jugador2: $puntosP2 Pts"
            }

            iv1.isEnabled= false
            iv2.isEnabled= false
        }else{
            //Poner la imagen reversa
            iv1.setImageResource(R.drawable.cartainicial)
            iv2.setImageResource(R.drawable.cartainicial)

            turno= if(turno == 1) 2 else 1
            Toast.makeText(baseContext, "Turno jugador $turno", Toast.LENGTH_LONG).show()

        }

        //Habilitar las cartas nuevamente
        cardImages.forEach { if(it.isEnabled) it.isEnabled=true }

        //Reiniciar variables para el turno siguiente
        primeraCarta= -1
        segundaCarta= -1

        //Verificar si el juego termino
        if(puntosP1 + puntosP2 == (cartasId.size/2)) {
            showWinner()
        }
    }

    //Metodo para mostrar ganador
    private fun showWinner(){
        val message= when{
            puntosP1>puntosP2 -> "Jugador 1 Gana"
            puntosP2>puntosP1 -> "Jugador 2 Gana"
            else-> "Empate"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    //Metodo para reiniciar juego
    private fun restartGame(){
        cartasId.shuffle()
        puntosP1=0
        puntosP2=0
        primeraCarta= -1
        segundaCarta= -1
        turno=1

        // Actualizar la interfaz
        binding.tvP1.text = "Jugador 1: 0 Pts"
        binding.tvP2.text = "Jugador 2: 0 Pts"

        //Volver a mostrar las cartas
        cardImages.forEach {
            it.setImageResource(R.drawable.cartainicial)
            it.isEnabled= true
        }
    }


}



