package com.katiacammisa.mycar.auth

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.fragment.app.FragmentActivity

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val canUseBiometric = remember(context) { viewModel.canAuthenticate(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Iniciar sesion")
        Text(text = "Accede con Face ID, huella o codigo del celular")

        if (canUseBiometric && activity != null) {
            Button(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.authenticate(activity, onAuthenticated)
                },
            ) {
                Text("Continuar")
            }
        } else {
            Text(
                text = "Este dispositivo no tiene biometria o codigo habilitado",
                modifier = Modifier.padding(top = 24.dp),
            )
        }

        viewModel.errorMessage?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

private tailrec fun Context.findActivity(): FragmentActivity? {
    return when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
