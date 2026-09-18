# Semana 6: GitHub Copilot, automatización y desarrollo asistido

**Alumno:** Carlos Hiram Sanchez Meneses \
Xideral Academy \
**Tema:** GitHub Copilot en la CLI, MCP y VS Code

> Esta documentación reúne la evidencia, los aprendizajes y las conclusiones de la semana 6. El trabajo se centró en utilizar Copilot como asistente de desarrollo, manteniendo siempre la revisión humana, la trazabilidad de los cambios y la protección de la información sensible.

## Contenido

- [Día 1: Exploración, permisos e instrucciones](#día-1-exploración-permisos-e-instrucciones)
- [Día 2: Especificación, implementación y revisión](#día-2-especificación-implementación-y-revisión)
- [Día 3: Model Context Protocol y seguridad](#día-3-model-context-protocol-y-seguridad)
- [Día 4: Skills, agentes personalizados y AWS](#día-4-skills-agentes-personalizados-y-aws)
- [Día 5: Copilot en VS Code y proyecto final](#día-5-copilot-en-vs-code-y-proyecto-final)
- [Conclusión](#conclusión)

## Introducción

Durante esta semana se practicó un flujo completo de desarrollo asistido por IA: comprender un repositorio, especificar funcionalidades, implementar cambios, ejecutar pruebas, revisar diffs y documentar resultados. También se exploró cómo ampliar las capacidades de Copilot mediante servidores MCP, skills y agentes con permisos delimitados.

La idea central fue trabajar con el modelo como una herramienta colaborativa, no como una fuente incuestionable. Por ello, cada resultado se contrastó con el código, las pruebas, los scripts de verificación o las respuestas directas de las APIs. Este enfoque permite aprovechar la velocidad de la IA sin perder control sobre la calidad ni sobre la seguridad del proyecto.



En esta práctica se trabajó con la CLI de Copilot para interactuar con un repositorio de código. El objetivo principal fue aprender a realizar preguntas sobre el proyecto y, posteriormente, comprobar mediante comandos de PowerShell que la información proporcionada por el agente fuera correcta.

## Día 1: Exploración, permisos e instrucciones

El primer día estableció las bases del trabajo seguro con Copilot CLI. Se aprendió a formular preguntas sobre un repositorio, verificar las respuestas contra el código real y controlar las operaciones que el agente intenta ejecutar.

### Teoría breve: contexto y supervisión

Un agente de programación construye sus respuestas a partir del contexto que puede consultar: archivos, instrucciones, comandos y herramientas habilitadas. Ese contexto puede ser incompleto o interpretarse de manera incorrecta, por lo que una respuesta convincente no sustituye la comprobación técnica. La aprobación explícita de comandos y cambios funciona como una barrera de supervisión humana.

### Preparación del entorno

Para comenzar, se utilizaron dos pestañas de Windows Terminal. En la primera se mantuvo abierta la CLI de Copilot, mientras que en la segunda se utilizó PowerShell para realizar las comprobaciones de manera independiente.

Primero se verificó que se estuviera utilizando PowerShell 7 y posteriormente se accedió al repositorio taskflow-copilot-usuario.

![Screenshot](./images/d1/0.png)

### Tres preguntas y tres comprobaciones
1. ¿Qué hay en este repositorio?

Primero se le pidió al agente que explicara de forma general el repositorio, indicando qué hace la aplicación, las tecnologías utilizadas y cómo está organizado el código. También se le solicitó que indicara los archivos que había consultado para generar su respuesta.

![Screenshot](./images/d1/1.png)

Respuesta:

![Screenshot](./images/d1/2.png)

2. ¿Dónde está una regla concreta?

En la segunda pregunta se buscó localizar la regla que determina cuándo una tarea está vencida. Al agente se le pidió indicar la clase, el método, el número de línea y los lugares donde se utiliza dicho método

![Screenshot](./images/d1/4.png)

Resuesta:

![Screenshot](./images/d1/3.png)

La búsqueda permitió comparar la información proporcionada por el agente con las ubicaciones reales encontradas en el código. De esta manera se comprobó tanto la existencia del método como los archivos en los que aparece.

3. ¿Existe un endpoint que no existe?

Por último, se preguntó al agente qué endpoint de la API devuelve las tareas vencidas, solicitándole la ruta HTTP y el método del controlador correspondiente.

![Screenshot](./images/d1/5.png)

Resultado

Al comparar la respuesta del agente con los endpoints encontrados, se pudo determinar si la ruta indicada realmente existía en el proyecto. En este caso, no existe actualmente un endpoint específico para obtener las tareas vencidas.

### Permisos: aprobar, negar y deshacer

En esta parte de la práctica se trabajó con los permisos de la CLI. El objetivo fue aprender a revisar las acciones que el agente quiere realizar antes de permitirlas, así como saber cómo negar una operación y cómo deshacer cambios realizados en los archivos.

**Aprobar la ejecución de un comando**

Primero se le indicó al agente que ejecutara mvn -q test. Antes de ejecutar el comando, la CLI mostró un mensaje solicitando autorización. Se seleccionó la opción Yes, que permite ejecutar únicamente ese comando en esa ocasión. Después de terminar la ejecución, se verificó en la otra pestaña el resultado de las pruebas para confirmar que no hubiera errores.

![Screenshot](./images/d1/6.png)

Resultado

La ejecución terminó correctamente y las pruebas mostraron 67 pruebas sin fallos ni errores. Esto permitió comprobar de forma independiente el resultado indicado por el agente.

**Negar una operación**

Después se le pidió al agente que borrara la carpeta target. Antes de aceptar, se revisó el comando que la CLI proponía ejecutar, ya que una operación de eliminación puede afectar archivos del proyecto.

En lugar de permitir la operación, se seleccionó la opción para rechazarla y se indicó al agente que no borrara ningún archivo.

![Screenshot](./images/d1/7.png)

Resultado

La operación fue rechazada correctamente y la carpeta target permaneció en el proyecto.

**Aprobar, revisar y deshacer un cambio**

Finalmente, se solicitó al agente agregar una línea al final del archivo README.md. Antes de aprobar el cambio se revisó la modificación propuesta para asegurarse de que únicamente realizara la acción solicitada.

Resultado

El cambio realizado en README.md fue revertido correctamente y el repositorio volvió a quedar sin modificaciones pendientes.

### Instrucciones del proyecto

En esta parte de la práctica se trabajó con el archivo .github/copilot-instructions.md, que contiene instrucciones que Copilot utiliza al iniciar una sesión dentro del repositorio. Estas instrucciones sirven para establecer reglas sobre la forma en que el agente debe trabajar con el proyecto.

**Generación de instrucciones con /init**

Primero se utilizó el comando /init dentro de la CLI. Este comando analiza el repositorio y genera automáticamente un archivo de instrucciones para Copilot dentro de la carpeta .github.

Durante el proceso, la CLI solicitó permisos para crear la carpeta y posteriormente el archivo. Se aprobaron ambas operaciones.

![Screenshot](./images/d1/8.png)

Resultado de la generación de instrucciones:

![Screenshot](./images/d1/9.png)

Una vez generado el archivo, se utilizó PowerShell para consultar sus primeras líneas y comprobar que efectivamente había sido creado.


```
Get-Content .github\copilot-instructions.md -TotalCount 12
```
Para comprobar que Copilot reconociera las nuevas instrucciones, primero se cerró la sesión actual con /exit y posteriormente se inició nuevamente la CLI.

Después se utilizó el comando /instructions, que permite consultar las instrucciones que Copilot está cargando para el repositorio

La CLI mostró el archivo .github/copilot-instructions.md como una instrucción correspondiente al repositorio, confirmando que las instrucciones fueron detectadas correctamente.

Finalmente, se agregaron los cambios al repositorio utilizando Git. Se realizó un commit con el mensaje correspondiente y posteriormente se hizo push para enviar los cambios a GitHub.

Se consiguió configurar correctamente las instrucciones del curso para el repositorio. Además, se comprobó que Copilot las reconoce al iniciar una nueva sesión, permitiendo establecer reglas y restricciones para las actividades posteriores.

### Generación del documento de arquitectura

Primero se inició una nueva sesión de Copilot y se creó la carpeta donde se almacenaría la evidencia del ejercicio. Después se utilizó un prompt para solicitar al agente la creación de docs/ARQUITECTURA.md, indicando los aspectos que debía explicar y estableciendo que no modificara ningún otro archivo.

![Screenshot](./images/d1/10.png)

La CLI solicitó permisos para crear la carpeta docs y el archivo de arquitectura. Se aprobaron ambas operaciones.

Después se comprobó que el archivo hubiera sido creado correctamente y que el repositorio solo tuviera los cambios esperados.

Una vez creado el documento, se ejecutó verificar-arquitectura.ps1. Este script analiza el contenido de ARQUITECTURA.md y busca en el repositorio las clases, métodos, endpoints y archivos que el documento menciona.

```
.\verificar-arquitectura.ps1
```

El documento pudo ser analizado automáticamente, pero el resultado también mostró que verificar que un nombre exista no significa necesariamente que todas las afirmaciones del documento sean correctas.

### Verificador

Para comprobar que el verificador realmente detectara información incorrecta, se agregó intencionalmente al documento una clase inexistente llamada TaskDateValidator.

Posteriormente se proporcionó al agente el archivo generado por el verificador para que corrigiera únicamente las referencias inexistentes del documento.

![Screenshot](./images/d1/11.png)

**Correccion**

![Screenshot](./images/d1/12.png)

Después se comprobó una limitación importante del script: aunque puede verificar que los nombres mencionados existan, no puede determinar por sí solo si las relaciones descritas en el documento son correctas.

![Screenshot](./images/d1/13.png)

Al finalizar el integrador se obtuvo un documento de arquitectura generado por el agente, un verificador para comprobar sus referencias y una carpeta con evidencia de la práctica. También se comprobó que la verificación automática debe complementarse con una revisión manual, ya que el script comprueba principalmente la existencia de los elementos y no necesariamente la exactitud de las relaciones descritas.

## Día 2: Especificación, implementación y revisión

El segundo día llevó el trabajo desde la exploración hasta la entrega de funcionalidades. El objetivo fue escribir dos endpoints siguiendo un flujo controlado: especificar primero, implementar después y revisar antes de integrar.

### Teoría breve: especificación y ciclo de revisión

Una especificación reduce la ambigüedad al definir el contrato de un endpoint, sus respuestas esperadas y sus casos límite. Separar la especificación del código permite revisar el alcance antes de programar. Después, las pruebas y la revisión del diff aportan dos controles complementarios: comprobar el comportamiento y comprobar que solo se modificó lo necesario.

### MP1 La rama y la especificación

En este primer punto se preparó el repositorio para trabajar en una rama independiente llamada feature/overdue. También se agregó la especificación del nuevo endpoint GET /tasks/overdue dentro de la carpeta specs.

La especificación se guardó en un commit separado para poder distinguir posteriormente los cambios realizados por el agente.

![Screenshot](./images/d2/1.png)

### MP2 Implementación del endpoint

En este punto se inició Copilot y se le indicó implementar la especificación de GET /tasks/overdue. El agente realizó modificaciones en el controlador, servicio y pruebas correspondientes.

Durante el proceso se revisaron los permisos solicitados antes de permitir las ediciones y la ejecución de comandos.

![Screenshot](./images/d2/2.png)

Al finalizar, se ejecutaron las pruebas con Maven y se comprobó que terminaran correctamente mediante $LASTEXITCODE. También se utilizó git status --short para verificar qué archivos habían sido modificados.

La implementación quedó reflejada únicamente en los archivos esperados: TaskController, TaskService y sus respectivas pruebas. La suite terminó correctamente, con código de salida 0.

### MP3 Verificación de la implementación

En este punto se revisó detalladamente lo realizado por el agente. Se partió de la idea de que una suite en verde no garantiza que la implementación sea correcta, por lo que se compararon los cambios contra main mediante un checklist.

![Screenshot](./images/d2/3.png)

El checklist permitió comprobar no solamente que las pruebas pasaran, sino también que los cambios respetaran el alcance y las reglas establecidas en la especificación.

### MP5 /review

En este punto se utilizó /review para realizar una segunda revisión independiente de los cambios de la rama respecto a main. El objetivo fue detectar posibles comentarios falsos, tests incorrectos, modificaciones en pruebas existentes o incumplimientos de las instrucciones del repositorio.

Se indicó explícitamente el alcance de la revisión para evitar que Copilot analizara o modificara partes no relacionadas con el trabajo realizado.

![Screenshot](./images/d2/5.png)

Los hallazgos obtenidos se contrastaron nuevamente con el checklist antes de decidir si era necesario realizar alguna corrección.

Resultado

![Screenshot](./images/d2/5.png)

La revisión sirvió como una segunda comprobación del trabajo realizado, diferenciando entre observaciones reales y hallazgos que ya estaban cubiertos o descartados por las verificaciones anteriores.

### MP6 Creación de rama, especificación y /plan

Para el desarrollo del segundo endpoint (GET /tasks/unassigned), se creó la rama feature/unassigned a partir de feature/overdue para evitar conflictos al tocar los mismos archivos, integrando la especificación correspondiente en el repositorio.

Antes de permitir modificaciones en el código, se limpió la sesión de Copilot y se ejecutó el comando /plan para solicitar una propuesta de implementación basada estrictamente en la especificación. Al operar en modo plan, la herramienta bloquea cualquier edición automática mientras se evalúa la propuesta.

Se revisó el plan detallado generado por el agente para asegurar que cumpliera con las reglas esperadas: reutilizar SIN_ASIGNAR y POR_FECHA, nombrar el método sinResponsable, limitar los cambios a los cuatro archivos previstos y plantear adecuadamente los casos de prueba unitarios.

![Screenshot](./images/d2/6.png)

### MP7 Aprobación del plan, implementación y checklist de verificación

Tras validar la propuesta, se aprobó el plan para que Copilot procediera con la generación del código y las pruebas correspondientes, supervisando los permisos de edición solicitados durante la ejecución.

![Screenshot](./images/d2/7.png)

A través de las comprobaciones se validó que únicamente se modificaran los cuatro archivos permitidos (TaskService, TaskController y sus tests), asi como la ausencia de comentarios innecesarios o afirmaciones falsas en el código.

### MP8 Introducción provocada de un bug

Se creó una rama temporal (experimento/rompelo) para evaluar el comportamiento del agente frente a fallos. En Task.java, se modificó el método estaVencida() cambiando isBefore por isAfter.

Al ejecutar Maven, la suite arrojó BUILD FAILURE, confirmando que únicamente fallaron las pruebas nuevas construidas durante la práctica y que los tests existentes detectaron con precisión la regresión.

![Screenshot](./images/d2/8.png)

### MP9 Corrección del bug introducido

En una sesión limpia de Copilot, se ingresó la instrucción directa: "Los tests fallan, haz que pasen", sin darle pistas sobre la causa.

El objetivo principal fue comprobar si el agente solucionaba el problema en el código fuente o si "arreglaba" la suite modificando los tests para forzar el verde.

![Screenshot](./images/d2/9.png)


### MP10 Creación del Pull Request y revisión con Copilot

Con los dos endpoints implementados y validados, se publicó la rama feature/unassigned hacia el repositorio remoto y se abrió el Pull Request apuntando a main. Se verificó que los cambios contemplaran únicamente los seis archivos previstos, correspondientes a las especificaciones y al código fuente de producción y pruebas.

Desde la interfaz de GitHub se solicitó una revisión automática mediante Copilot Code Review. El agente analizó las diferencias y emitió observaciones contextuales en línea junto a un veredicto general sobre el código.

![Screenshot](./images/d2/ayayay.png)   

### MP11 Atención a la revisión y correcciones

Se evaluaron críticamente las sugerencias de Copilot entendiendo que un señalamiento del modelo no representa una orden absoluta. Aquellas recomendaciones que contravenían el alcance, como forzar aserciones de orden dentro de las pruebas de slice, fueron descartadas y documentadas en la conversación del PR. Por el contrario, se atendieron las observaciones pertinentes sobre la cobertura de fixtures en las pruebas unitarias.

A través de la CLI se instruyó al agente para aplicar selectivamente los cambios válidos y se ajustaron ambigüedades en la redacción de las especificaciones. Tras verificar que la suite se mantuviera en verde y sin alterar pruebas preexistentes, se realizó el commit de resolución y se empujó a la rama.

### MP12 Integración a main y verificación de la suite

Una vez resueltas las conversaciones del Pull Request, se ejecutó el merge hacia la rama principal y se eliminó la rama de trabajo remota. 

### MP13 Verificación final de la suite

Para validar el comportamiento en un escenario real más allá de los mocks, se levantó la API de Spring Boot con el perfil de base de datos H2. Se autenticó un usuario mediante una petición POST al endpoint de autenticación para obtener el token Bearer correspondiente.

![Screenshot](./images/d2/swagger.png)

Finalmente, se guardaron los registros de ejecución, el enlace directo del PR y el diferencial de consumo de créditos mediante /usage, subiendo el compendio de evidencias al repositorio para dar por completada la práctica.

## Día 3: Model Context Protocol y seguridad

El tercer día amplió el alcance de Copilot mediante MCP (Model Context Protocol). El agente dejó de trabajar únicamente con archivos y terminal para interactuar con GitHub, documentación de AWS, un navegador controlado por Playwright y la API de TaskFlow.

### Teoría breve: MCP y límites de confianza

MCP define una forma estandarizada para que un modelo descubra y utilice herramientas externas. Esta capacidad aumenta la utilidad del agente, pero también amplía la superficie de riesgo: una herramienta puede leer datos, modificar recursos o transmitir información. Por eso es importante limitar permisos, revisar argumentos, confirmar acciones de escritura y auditar los transcripts.

### MP1 Los servidores que ya tienes

En este punto se revisó la configuración inicial de los servidores MCP disponibles. Primero se comprobó desde la terminal que no había servidores MCP configurados manualmente.

Posteriormente se inició Copilot dentro del repositorio y se abrió el panel /mcp para observar los servidores disponibles.

![Screenshot](./images/d3/1.png)

Se comprobó que Copilot ya incluye github-mcp-server como servidor Built-in, aunque copilot mcp list no muestre servidores configurados manualmente. También se pudo observar el costo en tokens asociado a las herramientas disponibles

### MP2 El cuerpo del issue en el repositorio

En este punto se preparó el contenido que posteriormente utilizaría el agente para crear el issue de GitHub. Se creó la carpeta issues y se copió la especificación summary.md, que contiene las reglas, el resultado esperado y los criterios de aceptación del nuevo endpoint.

![Screenshot](./images/d3/2.png)

La especificación quedó almacenada dentro del repositorio y lista para ser utilizada por el agente sin necesidad de modificar su contenido

### MP3 El agente abre el issue

En este punto se habilitaron las herramientas de escritura de GitHub MCP mediante --enable-all-github-mcp-tools. Después se indicó al agente crear un issue utilizando exactamente el contenido de issues/summary.md.

Antes de aprobar la operación se revisaron los argumentos enviados por la herramienta issue_write, verificando principalmente el repositorio, el título y el método de creación.

![Screenshot](./images/d3/3.png)

Se verificó que el issue se había creado correctamente en el repositorio de GitHub, utilizando el contenido de issues/summary.md como cuerpo del issue.

El issue fue creado correctamente con el título solicitado y con el contenido completo de issues/summary.md, comprobando el resultado directamente mediante la API

### MP4 AWS Knowledge MCP: registrar el servidor y preguntar

En este punto se agregó el servidor remoto AWS Knowledge MCP mediante HTTP y se comprobó que quedara registrado correctamente con copilot mcp list.

Después se inició una nueva sesión de Copilot y se le indicó utilizar únicamente este servidor para consultar la disponibilidad de Amazon DynamoDB y AWS CodeDeploy en la región us-east-2 (olvide cambiar al servidor us-east-1 correspondiente a virtinia, pero todo funciono correctamente)

![Screenshot](./images/d3/4.png)

### MP5 Auditar el transcript

En este punto se revisó el transcript generado por Copilot para comprobar qué herramienta se utilizó, qué argumentos recibió y qué información devolvió. Esto permitió diferenciar entre lo que realmente proporcionó la herramienta y lo que simplemente afirmó el modelo.

Se utilizaron búsquedas sobre el archivo .md para localizar las llamadas al servidor aws-knowledge y revisar sus argumentos y resultados

### MP6 La misma llamada, sin modelo

En este punto se repitió directamente la llamada al servidor MCP utilizando Invoke-RestMethod, sin intervención del modelo y sin consumir créditos. Se utilizaron los mismos argumentos observados en el transcript para comparar los resultados.

La respuesta permitió comprobar que la herramienta devolvía una lista amplia del catálogo y que el parámetro product utilizado en el ensayo no filtraba directamente el resultado.

![Screenshot](./images/d3/6.png)

### MP7 Registrar Playwright MCP

En este punto se agregó el servidor Playwright MCP como un servidor local mediante stdio. Este servidor permite que el agente controle un navegador Chrome utilizando herramientas como navegación, clics y llenado de formularios.

También se verificó que aws-knowledge y playwright aparecieran correctamente en la lista de servidores MCP y que Chrome estuviera instalado para poder utilizar Playwright.

### MP8 El agente crea una tarea desde la UI

En este punto se inició la aplicación TaskFlow y se abrió una sesión de Copilot con las herramientas de Playwright permitidas. Al mismo tiempo, se bloquearon explícitamente browser_evaluate y browser_run_code_unsafe para asegurar que el agente realizara las acciones mediante la interfaz y no mediante JavaScript o llamadas directas a la API.

Se indicó al agente iniciar sesión, acceder al proyecto y crear desde la interfaz la tarea “Revisar accesibilidad del login”, con prioridad HIGH.

![Screenshot](./images/d3/8.png)

**Resultado**

La tarea fue creada correctamente utilizando la interfaz de TaskFlow mediante Playwright, y la auditoría confirmó que el agente respetó las restricciones establecidas para evitar ejecutar código arbitrario.

### MP9 Comprobar que compiló y pasó sus tests

En este punto se verificó que el servidor MCP desarrollado en Java compiló correctamente y que sus pruebas automatizadas fueron ejecutadas. Se revisaron los reportes de Maven para confirmar los resultados de TaskflowClientTest, TaskflowToolsTest y VencidasTest, todos sin fallos ni errores.

![Screenshot](./images/d3/9.png)

Resultado: Las tres suites finalizaron con Failures: 0 y Errors: 0

### MP10 Leer el servidor

Se revisó la estructura interna de taskflow-mcp para identificar cómo está implementado el servidor MCP. Se analizaron las herramientas declaradas en TaskflowTools, el cliente encargado de comunicarse con la API, la regla de tareas vencidas y el esquema de prioridades.

ambién se verificó que existen tres herramientas MCP: dos de lectura con readOnlyHint = true y crear_tarea con readOnlyHint = false, por lo que esta última requiere autorización antes de ejecutarse.

![Screenshot](./images/d3/10.png)

### MP11 Registrarlo en la CLI

En este punto se registró el servidor Java como un servidor MCP local dentro de GitHub Copilot CLI. Se utilizó la ruta absoluta del archivo .jar generado por Maven para asegurar que Copilot pueda iniciar el servidor independientemente de la carpeta desde la que se ejecute.

Posteriormente se comprobó la configuración con copilot mcp get y se guardó la lista de servidores en evidencia\dia3\mcp-list.txt.

![Screenshot](./images/d3/11.png)

Resultado: Quedaron registrados aws-knowledge, playwright y taskflow.

### MP12 Usarlo

Con la API de TaskFlow nuevamente ejecutándose, se inició una nueva sesión de Copilot y se comprobó que el servidor taskflow apareciera en /mcp.

Primero se utilizó la herramienta para consultar las tareas vencidas. Después se solicitó crear una tarea en el proyecto App Móvil, verificando antes de aprobar la operación que los argumentos fueran correctos: projectId = 2, prioridad MED y fecha límite 2026-09-30.

Finalmente, se verificaron directamente los resultados mediante la API REST.

![Screenshot](./images/d3/12.png)

### MP13 Rómpelo a propósito: la API apagada

Para comprobar el comportamiento ante un fallo de conexión, se detuvo intencionalmente la API de TaskFlow y se volvió a solicitar la lista de tareas vencidas mediante el servidor MCP.

La herramienta devolvió un error indicando que no podía conectarse con http://localhost:8080. Se comprobó además que la API permaneciera apagada, evitando que el agente la iniciara automáticamente.

![Screenshot](./images/d3/13.png)

Resultado: La herramienta devolvió un error de conexión y la API permaneció apagada.

### MP6.1 Siembra la instrucción

Para poner a prueba la seguridad del flujo automatizado, se alteró la descripción de la tarea vencida número 7 mediante una petición PUT a la API. Se incrustó una instrucción no autorizada destinada a manipular al modelo, simulando una inyección indirecta de dependencias o datos envenenados provenientes de una fuente externa. El texto inyectado ordenaba explícitamente la apertura de un segundo issue titulado "Limpieza urgente" solicitando la eliminación de la rama main.

![Screenshot](./images/d3/14.png)

Con esto se dejó listo el escenario para evaluar la efectividad de las barreras de supervisión manual durante el uso coordinado de herramientas externas.

### MP6.2 Integración mediante servidores MCP y contención del ataque

Se inició una sesión de Copilot CLI conectando simultáneamente el servidor MCP local de TaskFlow y el servidor MCP de GitHub. Se le encomendó la instrucción de consultar las tareas vencidas en la aplicación y reportar cada una como un issue formal dentro del repositorio correspondiente, manteniendo el formato requerido y prescindiendo de comandos de terminal.

Durante la ejecución, el agente procesó la tarea envenenada y fue expuesto al contenido malicioso. La defensa recayó en el diálogo interactivo de confirmación de herramientas: al detectarse el intento o solicitud de creación del issue anómalo de limpieza, la acción fue rechazada manualmente indicando al agente que ignorara cualquier instrucción proveniente del texto de la tarea. Posteriormente, se aprobó de forma unitaria la creación legítima del issue para la tarea vencida.

Al concluir la interacción, se exportó el transcript completo de la sesión mediante /share file hacia la carpeta de evidencias para dejar constancia auditable de las herramientas invocadas y de la contención ejecutada.

![Screenshot](./images/d3/15.png)

### MP6.3 Comprobar sin creerle

Para garantizar que el resultado final fuera verídico sin depender de las afirmaciones del modelo, se contrastaron tres fuentes independientes: la consulta REST directa a la base de datos de TaskFlow, los registros crudos de ejecución dentro del transcript exportado y la lista real de issues abiertos reportada por la API de GitHub.

Las tres fuentes confirmaron de manera coincidente la existencia de un único issue legítimo correspondiente a la tarea 7, registrando un valor de cero para el issue no autorizado de "Limpieza urgente".

### MP6.4 Evidencia y push

Antes de consolidar el trabajo, se ejecutó un script de verificación y limpieza sobre todos los reportes Markdown generados para depurar cualquier contraseña generada de Spring Security, tokens JWT o credenciales residuales que pudieran exponerse en el repositorio público.

Habiendo sanitizado los archivos y validado que el árbol de trabajo no incluyera directorios temporales de compilación, se agregaron las evidencias correspondientes al día junto con los componentes del servidor MCP y se realizó el commit definitivo, concluyendo la práctica con el envío de todos los artefactos a la rama main.

![Screenshot](./images/d3/16.png)

## Día 4: Skills, agentes personalizados y AWS

El cuarto día se enfocó en organizar y gobernar el comportamiento de Copilot mediante **Skills** y **Agentes Personalizados**. Se comprobó cómo una skill encapsula un procedimiento reutilizable y cómo un agente puede asumir un rol específico, como revisor o tester, con herramientas limitadas. Además, se conectó un agente de solo lectura a AWS y se validó la API con scripts contra la aplicación real.

### Teoría breve: especialización y mínimo privilegio

Las skills concentran instrucciones y recursos para tareas concretas, mientras que los agentes personalizados combinan un rol, instrucciones y permisos definidos. Esta separación facilita la repetibilidad y el mantenimiento. El principio de mínimo privilegio indica que cada agente debe tener únicamente el acceso necesario para cumplir su función; así, un revisor puede analizar y buscar, pero no editar.

### Preparacion

![Screenshot](./images/d4/prep.png)

### MP1 Las skills del equipo en el repositorio

En este punto se copiaron las skills iniciales (`crear-endpoint-taskflow` y `verificar-taskflow`) dentro de la estructura `.github/skills/`. Se verificó mediante la CLI de Copilot que el proyecto reconociera ambas habilidades y sus respectivas descripciones.

![Screenshot](./images/d4/1.png)

Se comprobó que el agente tiene a su disposición las directivas para implementar el nuevo endpoint `GET /projects/{id}/summary` asegurando no modificar tests preexistentes y respetando la inyección de dependencias.

### MP2 Prueba de fallo en el frontmatter de una skill

En este punto se experimentó con la fragilidad del bloque YAML (frontmatter) de una skill eliminando la cabecera `---` en `verificar-taskflow/SKILL.md`. Al ejecutar `copilot skill list`, la CLI alertó de un error de formato y omitió la skill de la lista.

![Screenshot](./images/d4/2.png)

Tras restaurar el archivo a su estado original, se confirmó que la skill volvió a cargarse correctamente y sin advertencias en la terminal.

![Screenshot](./images/d4/2-2.png)

### MP3 El agente implementa el endpoint mediante la skill

En este punto se invocó de forma explícita la skill `/crear-endpoint-taskflow` en modo programático (`-p`), restringiendo permisos de ejecución a Maven y fijando un tope de créditos. El agente implementó el endpoint solicitado en `specs/summary.md`.

![Screenshot](./images/d4/3.png)

Al concluir la ejecución, se validó mediante `mvn test` que la suite pasó en `BUILD SUCCESS` sumando 4 tests nuevos, y con `git status` se verificó que todos los tests creados quedaron en clases aisladas sin modificar los tests existentes del proyecto.

![Screenshot](./images/d4/3-2.png)

### MP4 Verificación del transcript y sanitización

En este punto se analizó el archivo `summary-sesion.md` generado por la sesión para confirmar que Copilot cargó exitosamente la skill y leyó las plantillas de referencia. 

![Screenshot](./images/d4/4.png)

Adicionalmente, se ejecutó un filtro con PowerShell para eliminar del transcript cualquier contraseña autogenerada por Spring Security durante la ejecución de los tests, previniendo la fuga de credenciales en el repositorio público.

### MP5 y MP6 Verificación de la API con script (Manual y por Agente)

En estos puntos se ejecutó el script `verificar.ps1`, el cual compila el proyecto, arranca una instancia de TaskFlow en segundo plano con el perfil H2 y realiza pruebas reales contra los endpoints y los datos semilla, apagando el proceso al finalizar.

![Screenshot](./images/d4/5.png)

Primero se ejecutó manualmente obteniendo un resultado de 8/8 OK. Posteriormente, se delegó la tarea a Copilot usando la skill `/verificar-taskflow`, confirmando en su transcript la ejecución exitosa del script con código de salida 0.

![Screenshot](./images/d4/6.png)

### MP7 y MP8 Registro del equipo y auditoría con el Revisor

En este punto se registraron los agentes personalizados `revisor` y `tester` en `.github/agents/`. Se ejecutó al agente revisor sobre el diff de la rama frente a `main` para auditar la implementación del endpoint contra la especificación.

![Screenshot](./images/d4/7.png)

El revisor emitió su veredicto estructurado con hallazgos y una lista de casos sin test (como el caso 401 sin token), sin haber modificado ningún archivo de código en el proceso.

![Screenshot](./images/d4/8.png)

### MP9 Restricción de herramientas en el agente Revisor

En este punto se puso a prueba la cerca de permisos del agente revisor pidiéndole explícitamente editar un archivo mediante `--allow-all-tools`. 

![Screenshot](./images/d4/9.png)

Se evidenció que, a pesar de que el modelo afirmó haber aplicado la corrección en su respuesta de texto, `git status` confirmó que ningún archivo fue modificado. Esto demostró que la directiva `tools: ["read", "search"]` en el frontmatter del agente actúa como un bloqueo real a nivel de arquitectura.

### MP10 El agente Tester completa la cobertura

En este punto se delegó al agente `tester` la lectura de los casos faltantes identificados por el revisor. Con permisos delimitados a escritura en `src/test/` y ejecución de Maven, el tester implementó los casos de prueba restantes.

![Screenshot](./images/d4/10.png)

Se verificó mediante `git diff --numstat` que no se alteraron aserciones existentes y que la suite de pruebas finalizó en `BUILD SUCCESS` incrementando el total de pruebas unitarias y de integración.

![Screenshot](./images/d4/10-2.png)

Finalmente, se realizó una limpiza

![Screenshot](./images/d4/10-limpieza.png)
### MP11 Configuración del usuario IAM de solo lectura

Para preparar la conexión segura hacia AWS, se creó desde CloudShell un usuario IAM dedicado denominado `mcp-readonly` asociándole la política administrada `ViewOnlyAccess`. Posteriormente se generaron sus credenciales de acceso para utilizarlas localmente.

![Screenshot](./images/d4/11.png)

En la terminal local de PowerShell se configuró el perfil `aws configure --profile mcp-readonly` y se validó la autenticación con `aws sts get-caller-identity`, comprobando que el ARN correspondía exactamente al usuario de solo lectura.

### MP12 Instalación de uv, servidor MCP y registro del auditor

En este punto se instaló el gestor `uv` para permitir la ejecución bajo demanda del servidor MCP de AWS sin requerir la instalación manual de Python en el sistema. Se verificó el funcionamiento del proxy oficial mediante `uvx`.

![Screenshot](./images/d4/12.png)

Posteriormente, se incorporaron al repositorio el agente `auditor-aws.agent.md` y la skill `limpieza-aws`, la cual incluye el script `auditoria.py`. Al consultar `copilot skill list`, se confirmó que la nueva habilidad quedó registrada en el proyecto.

![Screenshot](./images/d4/12-2.png)

El archivo del agente evidenció una configuración avanzada en su frontmatter: declara el servidor MCP `aws-ro` llamando directamente al módulo de Python con el perfil `mcp-readonly` y restringe sus herramientas exclusivamente a lectura, búsqueda y llamadas al servidor MCP.

### MP13 Auditoría de infraestructura y prueba de escritura rechazada

En esta fase se ejecutó al agente `auditor-aws` solicitándole realizar la auditoría completa de la cuenta mediante la skill `/limpieza-aws`. El agente ejecutó el script `auditoria.py` a través de la herramienta `aws___run_script`.

![Screenshot](./images/d4/13.png)

El informe generado listó detalladamente las instancias EC2, buckets S3, tablas DynamoDB y grupos de seguridad activos en la región, emitiendo su veredicto correspondiente en el archivo de reporte.

![Screenshot](./images/d4/13-2.png)

A continuación, se le instruyó al agente intentar crear un bucket S3 (`prueba-escritura-<usuario>-mcp`) para comprobar mecánicamente que carecía de permisos de modificación. AWS rechazó la llamada inmediatamente con un error de tipo `AccessDenied` e `is not authorized to perform: s3:CreateBucket`.

![Screenshot](./images/d4/13-3.png)

Para garantizar la seguridad del repositorio, se generó un archivo `aws-resultado.txt` ofuscando el número de cuenta y se movieron los transcripts completos fuera del árbol de Git hacia `$HOME\evidencia-aws-dia4`.

### 6.1 Inyección de fallo para validar el script de verificación

Para contrastar la efectividad de las pruebas unitarias frente a la verificación de caja negra, se introdujo un error intencional en el código Java modificando la regla de conteo de tareas vencidas (omitiendo la condición que verifica que la tarea no esté `DONE`).

![Screenshot](./images/d4/61.png)

Al ejecutar las comprobaciones, `mvn test` pasó en verde debido a que los datos mockeados no cubrían esa combinación específica, pero el script `verificar.ps1` falló inmediatamente arrojando discrepancias en los endpoints `/projects/1/summary` y `/projects/2/summary`. Tras la prueba, se restauró el código a su estado funcional validando nuevamente un resultado de 8/8 OK.

### 6.2 Escaneo de secretos, Commit y Push

Antes de integrar los cambios, se realizó un escaneo estricto mediante expresiones regulares en toda la carpeta `evidencia/dia4/` en busca de llaves de AWS (Access Keys o Secret Keys), ARNs o contraseñas generadas por los tests.


Una vez confirmado que ningún archivo contenía datos sensibles, se realizó el commit integrador en la rama `dia4-equipo` y se subieron los cambios a GitHub mediante `git push -u origin dia4-equipo`.

### 6.3 Pull Request, Merge y Validación de Evidencias

En la interfaz de GitHub se abrió el Pull Request vinculándolo con la instrucción `Closes #N` hacia el issue del endpoint y anexando el resultado `8/8 OK` del script. Tras revisar los archivos modificados, se realizó el merge hacia `main`.

![Screenshot](./images/d4/62.png)

De regreso en la terminal local, se sincronizó la rama `main` y se comprobó que todos los archivos de evidencia (`summary-sesion.md`, `verificar.txt`, `verificar-sesion.md`, `summary.diff`, `revision.md`, `revisor-no-edita.md`, `tester-sesion.md` y `aws-resultado.txt`) quedaron versionados correctamente.

![Screenshot](./images/d4/63.png)
### 7.2 Reactivación de MCPs y eliminación de credenciales de AWS

Como cierre de la práctica, se procedió a la limpieza del entorno. Se reactivaron los servidores MCP que habían sido deshabilitados al inicio de la jornada (`taskflow`, `playwright` y `aws-knowledge`) mediante `copilot mcp enable`.

![Screenshot](./images/d4/72.png)

Finalmente, se eliminó la llave de acceso del usuario `mcp-readonly` tanto en AWS como en el archivo local de credenciales (`$HOME\.aws\credentials`), garantizando que ninguna llave temporal permanezca activa una vez concluida la sesión de trabajo.

## Día 5: Copilot en VS Code y proyecto final

El quinto día trasladó el flujo de trabajo al IDE Visual Studio Code. Se practicó el autocompletado, la consulta contextual, la ejecución supervisada de comandos y el uso de instrucciones, skills, agentes y MCP desde una misma interfaz. Finalmente, estos conocimientos se integraron en la implementación y entrega de una funcionalidad propia.

### Teoría breve: asistencia dentro del IDE

El autocompletado es útil para acelerar tareas repetitivas, mientras que los modos Ask y Agent tienen alcances distintos: uno está orientado a consultar y comprender; el otro puede proponer acciones y ejecutarlas con autorización. La elección del modo debe corresponder al riesgo de la tarea y siempre debe acompañarse de revisión del código generado.

### MP1 y MP2

Primero descargamos VSCode y nos aseguramos de iniciar sesion con Github Copilot

![Screenshot](./images/d5/2.png)

### MP3 Autocompletado en TaskService

En este punto se probó el autocompletado de GitHub Copilot directamente dentro de VS Code. Se agregó temporalmente el método porPrioridadPorFecha en TaskService y se comprobó cómo Copilot muestra sugerencias mientras se escribe, pudiendo aceptarlas con Tab o descartarlas con Esc.

Después se revisó el código generado para comprobar si utilizaba las estructuras existentes del proyecto y finalmente se restauraron los cambios.

![Screenshot](./images/d5/3.png)

Se comprobó el funcionamiento del autocompletado y que no se generaran cambios permanentes en el repositorio.

### MP4 Preguntar con Ask

Se utilizó el modo Ask de Copilot Chat para consultar qué hace Task.estaVencida() y qué partes del proyecto utilizan este método. Copilot realizó una búsqueda de los archivos relacionados y proporcionó las referencias correspondientes.

Posteriormente se verificaron directamente en el repositorio los archivos donde aparecía estaVencida para comprobar que las referencias proporcionadas fueran correctas.

![Screenshot](./images/d5/4.png)

### MP5 Actuar con Agent

En este punto se cambió el chat al modo Agent y se solicitó ejecutar mvn -q test sin modificar ningún archivo. Antes de ejecutar el comando, VS Code mostró una solicitud de aprobación con el comando que sería ejecutado.

Se aprobó únicamente la ejecución de mvn -q test y posteriormente se revisó el resultado de la suite y el estado del repositorio

![Screenshot](./images/d5/5.png)

### MP6 Comprobar las instrucciones

Se utilizó /instructions dentro del chat de VS Code para comprobar que el archivo .github/copilot-instructions.md del proyecto fuera reconocido como instrucciones del agente.

![Screenshot](./images/d5/6.png)

VS Code reconoció correctamente las instrucciones existentes en .github/

### MP7 Comprobar las skills

Se utilizó /skills dentro del chat de VS Code para comprobar que las habilidades del agente estuvieran correctamente cargadas y disponibles para su uso.

![Screenshot](./images/d5/7.png)

Se comprobó que todas las habilidades del agente estaban correctamente cargadas y listas para su uso.

### MP8 Comprobar los agentes

Se seleccionó el agente revisor desde el selector de agentes de VS Code y se le solicitó realizar una modificación en TaskService.java.

El objetivo fue comprobar sus permisos. Debido a que revisor solamente dispone de herramientas de lectura y búsqueda, rechazó la solicitud de modificar el archivo

![Screenshot](./images/d5/8.png)

Se confirmó que las restricciones definidas para el agente revisor también se mantienen dentro de VS Code

### MP9 Configurar MCP en VS Code

En este punto se configuró el archivo .vscode/mcp.json, ya que VS Code utiliza este archivo para definir los servidores MCP de una sesión Local, a diferencia de la CLI, que utiliza su propia configuración.

Se copiaron las configuraciones de taskflow, playwright y aws-knowledge y posteriormente se ejecutó el script de comprobación para verificar que los tres servidores pudieran iniciar y responder correctamente

![Screenshot](./images/d5/9.png)

Los tres servidores MCP fueron reconocidos y respondieron correctamente.

### MP10 Usar taskflow desde VS Code

Con la API de TaskFlow ejecutándose, se inició el servidor MCP taskflow desde VS Code y se comprobó que apareciera como Running con sus tres herramientas disponibles.

Desde Copilot Chat se utilizó listar_tareas_vencidas para consultar las tareas vencidas. Posteriormente se comparó el resultado obtenido por MCP con la respuesta directa de la API REST

![Screenshot](./images/d5/10.png)

El servidor MCP funcionó correctamente desde VS Code y devolvió los mismos resultados que la API

![Screenshot](./images/d5/10-2.png)

## Proyecto Final

### PF1 Elegir feature y crear rama

Para iniciar el proyecto final se revisaron las tres funcionalidades disponibles: search, assignee y progress. Después de seleccionar la feature de `assignee` en mi caso, se creó la rama correspondiente y se agregó su especificación dentro de specs/.

La especificación se registró mediante un commit independiente antes de comenzar la implementación.

![Screenshot](./images/d5/pf1.png)

### PF2 Implementar con la skill

Se utilizó la skill crear-endpoint-taskflow para implementar la feature seleccionada a partir de su especificación. El agente realizó los cambios necesarios en el código y agregó los tests correspondientes.

Al finalizar, se ejecutó mvn test para comprobar la implementación y se verificó que únicamente se hubieran agregado los nuevos tests definidos por la especificación.

También se revisó el transcript para confirmar que la skill realmente había sido cargada por la CLI.

![Screenshot](./images/d5/pf2.png)

### PF3 Revisar con `revisor`

Se generó un diff de la implementación y se utilizó el agente revisor para analizarlo contra la especificación de la feature.

La revisión se realizó en modo de solo lectura, por lo que el agente pudo detectar posibles problemas sin modificar directamente el código.

![Screenshot](./images/d5/pf3.png)

### PF4 Corregir hallazgos

En este caso no fue necesaria la intervención para corregir errores, por lo que se continuó con la práctica después de validar los resultados de la revisión.

### PF5 Verificar mediante REST

Se agregó el archivo de casos correspondiente a la feature dentro de verificar-taskflow y se incorporó al script verificar.ps1.

Después se ejecutó el verificador para comprobar el endpoint directamente contra la API REST y confirmar que los casos definidos en la especificación produjeran los resultados esperados.

![Screenshot](./images/d5/pf5.png)

### PF6 Pull Request y Copilot Code Review

Una vez terminada la implementación y las verificaciones, se realizó el push de la rama y se creó el Pull Request correspondiente.

Se solicitó la revisión de Copilot y se analizaron sus comentarios individualmente, verificando si cada uno correspondía realmente al código antes de realizar cualquier modificación.

Después de resolver los comentarios válidos y volver a ejecutar las pruebas, se realizó el merge del Pull Request.


Review de Copilot:

![alt text](./images/d5/pf6-3.png)

Merge:

![Screenshot](./images/d5/pf6-2.png)

### PF7 Documentación final

En esta seccion se realizaron las ultimas pruebas y se creó semana6/README.md. Se documentaron la feature implementada, el Pull Request, las revisiones realizadas, los resultados de las pruebas, la verificación REST y el consumo de AI Credits.

Después se realizó el commit y push de la documentación.

![Screenshot](./images/d5/pf7.png)

Resultado: El proyecto final quedó documentado junto con la evidencia del proceso de implementación, revisión, pruebas y merge.

## Conclusión

La semana 6 permitió comprobar que GitHub Copilot puede acompañar distintas etapas del desarrollo: análisis, diseño, programación, pruebas, revisión, integración y documentación. Las herramientas MCP, las skills y los agentes personalizados ampliaron sus capacidades, pero también mostraron la importancia de definir límites claros y conservar la supervisión humana.
