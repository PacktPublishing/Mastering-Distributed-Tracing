import React, { Component } from "react"
import { Button, MessageInput } from "./Styled"
export default class ChatInput extends Component {
  state = {
    message: ""
  }

  buttonRef = React.createRef()
  onChange(e) {
    this.setState({ message: e.target.value })
  }

  sendMessage(e) {
    e.preventDefault()
    this.props.sendMessage(this.state.message)
    this.setState({ message: "" })
  }

  componentDidUpdate() {
    this.buttonRef.current.scrollIntoView({ behavior: "smooth" })
  }

  render() {
    return (
      <form onSubmit={e => this.sendMessage(e)}>
        <MessageInput
          type="text"
          name="chatinput"
          value={this.state.message}
          placeholder="Enter message here... Try '/giphy <topic>'."
          autoComplete="off"
          onChange={e => this.onChange(e)}
        />
        <Button innerRef={this.buttonRef}>Send</Button>
      </form>
    )
  }
}
