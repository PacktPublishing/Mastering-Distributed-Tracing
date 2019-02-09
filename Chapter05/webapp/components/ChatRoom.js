import React, { Component } from "react"
import { RoomName, MessageList, HorizontalLayout } from "./Styled"
import ChatInput from "./ChatInput"
import Message from "./Message"

export default class ChatRoom extends Component {
  render() {
    return (
      <div>
        {/*<RoomName>{this.props.room.name}</RoomName>*/}
        <HorizontalLayout justifyContent="center">
          <MessageList>
            {this.props.room.messages.map(message => (
              <Message key={message.id} message={message} />
            ))}
          </MessageList>
        </HorizontalLayout>
        <HorizontalLayout justifyContent="center">
          <ChatInput sendMessage={this.props.sendMessage} />
        </HorizontalLayout>
      </div>
    )
  }
}
