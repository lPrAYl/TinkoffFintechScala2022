package ru.tinkoff.fintech.lession9.document

import java.util.UUID

case class Document(id: UUID, link: String, version: Int, creator: UUID, editor: UUID)
