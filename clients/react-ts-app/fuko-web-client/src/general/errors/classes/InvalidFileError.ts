export default class InvalidFileError extends Error {
  constructor(message: string) {
    super(message); // передаем сообщение в конструктор Error
    this.name = "InvalidFileError"; // задаем имя ошибки

    // Восстанавливаем прототип (необходимо для правильной работы instanceof в некоторых версиях JS/TS)
    Object.setPrototypeOf(this, InvalidFileError.prototype);
  }
}
