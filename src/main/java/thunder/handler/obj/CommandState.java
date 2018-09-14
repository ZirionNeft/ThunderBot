package thunder.handler.obj;

public enum CommandState {
    /*
    * When user may use command
    * */
    FREE,

    /*
    * When command is busy by translator for user
    * */
    TRANSLATE,

    /*
    * When user should do accept or deny the action
    * */
    ACCEPT_REMOVE
}
