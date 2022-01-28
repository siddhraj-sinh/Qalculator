package com.siddharaj.qalculator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.siddharaj.qalculator.databinding.ActivityMainBinding
import com.siddharaj.qalculator.model.HistoryModel
import com.siddharaj.qalculator.viewmodel.HistoryViewModel
import com.udojava.evalex.Expression
import java.util.*

class MainActivity : AppCompatActivity(), CellClickListener {


    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val onResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                var modeifyOutput = value?.get(0)
                modeifyOutput = modeifyOutput?.lowercase()
                modeifyOutput = modeifyOutput?.replace(" ", "")
                modeifyOutput = modeifyOutput?.replace("x", "*")
                modeifyOutput = modeifyOutput?.replace("X", "*")
                modeifyOutput = modeifyOutput?.replace("add", "+")
                modeifyOutput = modeifyOutput?.replace("sub", "-")
                modeifyOutput = modeifyOutput?.replace("to", "2")
                modeifyOutput = modeifyOutput?.replace("tu", "2")
                modeifyOutput = modeifyOutput?.replace("do", "2")
                modeifyOutput = modeifyOutput?.replace("for", "4")
                modeifyOutput = modeifyOutput?.replace("plus", "+")
                modeifyOutput = modeifyOutput?.replace("minus", "-")
                modeifyOutput = modeifyOutput?.replace("times", "*")
                modeifyOutput = modeifyOutput?.replace("into", "*")
                modeifyOutput = modeifyOutput?.replace("in2", "*")
                modeifyOutput = modeifyOutput?.replace("multiply by", "*")
                modeifyOutput = modeifyOutput?.replace("multiply", "*")
                modeifyOutput = modeifyOutput?.replace("divide by", "/")
                modeifyOutput = modeifyOutput?.replace("divide", "/")
                modeifyOutput = modeifyOutput?.replace("raised to", "^")
                modeifyOutput = modeifyOutput?.replace("raise to", "^")
                modeifyOutput = modeifyOutput?.replace("race2", "^")
                modeifyOutput = modeifyOutput?.replace("race to", "^")
                modeifyOutput = modeifyOutput?.replace("power", "^")
                modeifyOutput = modeifyOutput?.replace("equal", "=")
                modeifyOutput = modeifyOutput?.replace("equals", "=")
                modeifyOutput = modeifyOutput?.replace("equal to", "=")
                modeifyOutput = modeifyOutput?.replace("open bracket", "(")
                modeifyOutput = modeifyOutput?.replace("close bracket", ")")
                modeifyOutput = modeifyOutput?.replace("left bracket", "(")
                modeifyOutput = modeifyOutput?.replace("right bracket", ")")
                modeifyOutput = modeifyOutput?.replace("opening bracket", "(")
                modeifyOutput = modeifyOutput?.replace("closing bracket", ")")
                if (modeifyOutput?.contains("=")!!) {
                    modeifyOutput = modeifyOutput.replace("=", "")
                    binding.tvExpression.text = modeifyOutput
                    onEqual()
                } else {
                    binding.tvExpression.text = modeifyOutput
                }

            }

        }


    private lateinit var binding: ActivityMainBinding

    var lastNumeric: Boolean = false
    var stateError: Boolean = false
    var lastDot: Boolean = false
    private lateinit var mAdapter: HistoryAdapter
    var openBracket = true
    var isHistoryVisible = false
    private var shortAnimationDuration: Int = 0
    private lateinit var mHistoryViewModel: HistoryViewModel
    private lateinit var tempList: List<HistoryModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init note view model
        mHistoryViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        binding.btnEqual.setOnClickListener {
            onEqual()
        }
        binding.tvClearHistory.setOnClickListener {
            //todo: implement clear history feature.
            Toast.makeText(this,"Yet to Implement",Toast.LENGTH_SHORT).show();
        }
        mHistoryViewModel.history.observe(this,{
            if(it.isNotEmpty()){
                mAdapter = HistoryAdapter(this,it,this,mHistoryViewModel)
                binding.rvHistory.layoutManager = LinearLayoutManager(this)
                binding.rvHistory.adapter=mAdapter
                tempList=it
            }
        })
    }



    fun onDigit(view: View) {
        if (stateError) {
            binding.tvExpression.text = (view as Button).text
            stateError = false
        } else {
            binding.tvExpression.append((view as Button).text)
        }
        lastNumeric = true
    }

    fun onDecimalPoint(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            binding.tvExpression.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onClear(view: View) {
        binding.tvExpression.text = ""
        binding.tvOutput.text = ""
        openBracket = true

        lastNumeric = false
        stateError = false
        lastDot = false
    }

    private fun onEqual() {

        if (lastNumeric && !stateError) {
            val text = binding.tvExpression.text.toString()

            try {
                //val expression= ExpressionBuilder(text).build()
                // val result= expression.evaluate()
                val ex = Expression(text)

                val result = ex.eval().toString()
                binding.tvOutput.text = result
                mHistoryViewModel.insert(HistoryModel(0, text, result.toString()))
                lastDot = true
            } catch (ex: Exception) {
                binding.tvOutput.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

    fun onOperator(view: View) {
        if (lastNumeric && !stateError) {
            binding.tvExpression.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    fun onBackspace(view: View) {
        val text = binding.tvExpression.text.toString()
        if (text.isNotEmpty()) {
            binding.tvExpression.text = text.dropLast(1)
            binding.tvOutput.text = ""
        }
    }

    fun onSpeak(view: View) {
        if (!stateError) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getSpeechInput()
                lastNumeric = true
            } else {
                requestPermission.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        } else {
            binding.tvOutput.text = "Error"
        }
    }


    private fun getSpeechInput() {
        binding.tvExpression.text = ""
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        try {
            onResult.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Oops! your device doesn't support speech input",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    fun onBracket(view: View) {
        if (openBracket) {
            binding.tvExpression.append("(")
            openBracket = false
        } else {
            binding.tvExpression.append(")")
            openBracket = true
        }
    }

    fun onHistory(view: View) {

        if (!isHistoryVisible) {
            shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
            crossFade()
            isHistoryVisible = true

        } else {
            // binding.keyboard.visibility=View.VISIBLE
            // binding.history.visibility=View.GONE
            shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
            reverseCrossFade()
            isHistoryVisible = false
        }
    }


    private fun crossFade() {

        binding.keyboard.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.keyboard.visibility = View.GONE
                }
            })
        binding.history.apply {
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
        binding.ivHistory.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_calculator
            ),
        )
    }

    private fun reverseCrossFade() {
        binding.history.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.history.visibility = View.GONE
                }
            })
        binding.keyboard.apply {
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
        binding.ivHistory.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_history))
    }

    override fun onCellClickListener(data: HistoryModel) {
        val expression = data.expression
        val result = data.result
        binding.tvExpression.text = ""
        binding.tvOutput.text = ""
        binding.tvExpression.text = expression
        binding.tvOutput.text = result
        lastNumeric = true
    }
}