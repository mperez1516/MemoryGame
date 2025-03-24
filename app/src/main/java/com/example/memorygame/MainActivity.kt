package com.example.memorygame

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityMainBinding
import com.example.memorygame.databinding.ImagesBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imagesBinding: ImagesBinding

    private lateinit var cardImages: Array<ImageView>

    // Mapa que asocia el identificador de la carta con su imagen
    private val mapaCartas = mutableMapOf(
        101 to R.drawable.jirafa101, 102 to R.drawable.elefante102, 103 to R.drawable.tigre103,
        104 to R.drawable.leon104, 105 to R.drawable.rino105, 106 to R.drawable.bambi106,
        201 to R.drawable.jirafa201, 202 to R.drawable.elefante202, 203 to R.drawable.tigre203,
        204 to R.drawable.leon204, 205 to R.drawable.rino205, 206 to R.drawable.bambi206
    )

    // Lista mutable de identificadores de cartas (se mezclará en cada partida)
    private val cartasId = mapaCartas.keys.toMutableList()

    private var primeraCarta = -1
    private var segundaCarta = -1
    private var clickPrimero = -1
    private var clickSegundo = -1
    private var turno = 1
    private var puntosP1 = 0
    private var puntosP2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagesBinding = ImagesBinding.bind(binding.includeImages.root)

        binding.tvP1.text = "Jugador 1: 0 Pts"
        binding.tvP2.text = "Jugador 2: 0 Pts"

        // Inicializar array de ImageView
        cardImages = arrayOf(
            imagesBinding.iv11, imagesBinding.iv12, imagesBinding.iv13, imagesBinding.iv14,
            imagesBinding.iv21, imagesBinding.iv22, imagesBinding.iv23, imagesBinding.iv24,
            imagesBinding.iv31, imagesBinding.iv32, imagesBinding.iv33, imagesBinding.iv34
        )

        iniciarJuego()

        binding.btnRestart.setOnClickListener { restartGame() }
    }

    private fun iniciarJuego() {

        // Reiniciar puntuaciones
        puntosP1 = 0
        puntosP2 = 0
        turno = 1
        binding.tvP1.text = "Jugador 1: 0 Pts"
        binding.tvP2.text = "Jugador 2: 0 Pts"

        // Reiniciar cartas
        for (i in cardImages.indices) {
            cardImages[i].tag = cartasId[i]
            cardImages[i].setImageResource(R.drawable.cartainicial)
            cardImages[i].isEnabled = true
            cardImages[i].setOnClickListener { cardClicked(it) }
        }
    }

    private fun cardClicked(view: View) {
        val iv = view as ImageView
        val cardId = iv.tag as Int

        if (iv.drawable.constantState == resources.getDrawable(R.drawable.cartainicial).constantState) {
            mostrarCarta(iv, cardId)
        }
    }

    private fun mostrarCarta(iv: ImageView, cardId: Int) {
        iv.setImageResource(mapaCartas[cardId] ?: R.drawable.cartainicial)

        if (primeraCarta == -1) {
            primeraCarta = cardId
            clickPrimero = cartasId.indexOf(cardId)
        } else {
            segundaCarta = cardId
            clickSegundo = cartasId.indexOf(cardId)

            cardImages.forEach { it.isEnabled = false }

            Handler().postDelayed({ verificarPareja() }, 1000)
        }
    }

    private fun verificarPareja() {
        val iv1 = cardImages[clickPrimero]
        val iv2 = cardImages[clickSegundo]

        if (primeraCarta % 100 == segundaCarta % 100) {
            if (turno == 1) {
                puntosP1++
                binding.tvP1.text = "Jugador 1: $puntosP1 Pts"

            } else {
                puntosP2++
                binding.tvP2.text = "Jugador 2: $puntosP2 Pts"
            }
            iv1.isEnabled = false
            iv2.isEnabled = false
            continuarJuego()

        } else {
            iv1.setImageResource(R.drawable.cartainicial)
            iv2.setImageResource(R.drawable.cartainicial)

            turno = if (turno == 1) 2 else 1
            Toast.makeText(this, "Turno jugador $turno", Toast.LENGTH_LONG).show()
            continuarJuego()
        }

        cardImages.forEach { if (it.isEnabled) it.isEnabled = true }


        if (puntosP1 + puntosP2 == cartasId.size / 2) {
            mostrarGanador()
        }
    }

    private fun continuarJuego(){

        primeraCarta= -1
        segundaCarta= -1

        // Reiniciar cartas
        for (i in cardImages.indices) {
            cardImages[i].tag = cartasId[i]
            cardImages[i].isEnabled = true
            cardImages[i].setOnClickListener { cardClicked(it) }
        }
    }

    private fun mostrarGanador() {
        val mensaje = when {
            puntosP1 > puntosP2 -> "Jugador 1 Gana"
            puntosP2 > puntosP1 -> "Jugador 2 Gana"
            else -> "Empate"
        }
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    //Metodo para reiniciar juego
    private fun restartGame(){
        puntosP1=0
        puntosP2=0
        primeraCarta= -1
        segundaCarta= -1
        turno=1

        cartasId.shuffle()

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
