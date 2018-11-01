import React, { Component } from "react"
import ReactDOM from "react-dom"
import ChatRoom from "./components/ChatRoom"
import UserInput from "./components/UserInput"
import { sendMessage, getMessages } from "./actions/Messages"
import { Page, Header, HorizontalLayout } from "./components/Styled"
import ErrorBoundary from "./components/ErrorBoundary"

class App extends Component {
  state = {
    title: "Tracing Talk",
    user: "guest-" + Math.floor(Math.random() * 1000),
    room: {
      name: "lobby",
      messages: [
        {
          id: "a",
          date: "2018-08-16T08:00:31-04:00",
          author: "Ralph",
          message: "👋 Hey, there!"
        },
        {
          id: "b",
          date: "2018-08-16T08:02:31-04:00",
          author: "Johnny",
          message: "Hello!",
          image: "https://media.giphy.com/media/mIZ9rPeMKefm0/giphy.gif"
        }
      ]
    }
  }

  componentDidMount() {
    this.refreshMessages()
    this.interval = setInterval(() => {
      this.refreshMessages()
    }, 3000)
  }

  componentWillUnmount() {
    clearInterval(this.interval)
  }

  refreshMessages() {
    return getMessages().then(messages =>
      this.setState(state => {
        state.room.messages = messages
        return state
      })
    )
  }

  userInputChange(e) {
    this.setState({ user: e.target.value })
  }

  send(message) {
    if (message) {
      sendMessage(this.state.user, this.state.room.name, message).then(
        savedMessage => {
          this.setState(state => {
            state.room.messages.push(savedMessage)
            return state
          })
        }
      )
    }
  }

  render() {
    return (
      <HorizontalLayout justifyContent="center">
        <Page>
          <title>{this.state.title}</title>
          <HorizontalLayout>
            <Header>{this.state.title}</Header>
            <UserInput
              user={this.state.user}
              onChange={e => this.userInputChange(e)}
            />
          </HorizontalLayout>
          <ErrorBoundary>
            <ChatRoom
              room={this.state.room}
              messages={this.state.messages}
              sendMessage={message => this.send(message)}
            />
          </ErrorBoundary>
        </Page>
      </HorizontalLayout>
    )
  }
}

ReactDOM.render(<App />, document.getElementById("app"))
