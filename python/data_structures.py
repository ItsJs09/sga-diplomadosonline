from __future__ import annotations

from collections import deque
from typing import Deque, Generic, List, Optional, Tuple, TypeVar

T = TypeVar("T")

RegistroNota = Tuple[str, float]


class Pila(Generic[T]):

    def __init__(self) -> None:
        self._elementos: List[T] = []

    def apilar(self, elemento: T) -> None:
        self._elementos.append(elemento)

    def desapilar(self) -> T:
        if self.esta_vacia():
            raise IndexError("No es posible deshacer: no hay notas registradas en esta sesión.")
        return self._elementos.pop()

    def ver_tope(self) -> Optional[T]:
        if self.esta_vacia():
            return None
        return self._elementos[-1]

    def esta_vacia(self) -> bool:
        return len(self._elementos) == 0

    def tamanio(self) -> int:
        return len(self._elementos)


class Cola(Generic[T]):
    def __init__(self) -> None:
        self._elementos: Deque[T] = deque()

    def encolar(self, elemento: T) -> None:
        self._elementos.append(elemento)

    def desencolar(self) -> T:
        if self.esta_vacia():
            raise IndexError("No es posible desencolar: la cola está vacía.")
        return self._elementos.popleft()

    def esta_vacia(self) -> bool:
        return len(self._elementos) == 0

    def tamanio(self) -> int:
        return len(self._elementos)
