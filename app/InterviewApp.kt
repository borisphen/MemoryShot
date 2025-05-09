@Composable
fun InterviewApp() {
    val viewModel: InterviewViewModel = viewModel(factory = ViewModelFactory(Injection.provideUseCase()))
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.answerFlow.collectLatest { answer ->
            Log.d("InterviewApp", "Answer: $answer")
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Interview Assistant", style = MaterialTheme.typography.headlineMedium)
        }
    }
}