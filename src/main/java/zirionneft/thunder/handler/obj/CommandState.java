package zirionneft.thunder.handler.obj;

public enum CommandState {
    /*
    * When command is busy by translator for user
    * */
    TRANSLATE,

    /*
    * When user should do accept or deny the action
    * */
    ACCEPT_ROLE_REMOVE,

    /*
    * When user initiate coins transfer
    * */
    COINS_TRANSFER
}
