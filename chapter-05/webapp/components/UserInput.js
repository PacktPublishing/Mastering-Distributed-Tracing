import React, { Component } from "react"
import styled from "styled-components"

import {
  NicknameInput,
  Button,
  Author,
  HorizontalLayout,
  Space
} from "./Styled"

export default class UserInput extends Component {
  state = {
    isEditing: false
  }

  userRef = React.createRef()

  isEditing(e) {
    e.preventDefault()
    this.setState(state => {
      if (this.props.user) return { isEditing: !state.isEditing }
      else return state
    })
  }

  render() {
    return (
      <form onSubmit={e => this.isEditing(e)}>
        <HorizontalLayout>
          <label>Nickname</label>
          <Space marginLeft="0.5em" />
          {this.state.isEditing ? (
            <NicknameInput
              type="text"
              name="user"
              innerRef={this.userRef}
              value={this.props.user}
              placeholder="Enter username here..."
              onChange={e => this.props.onChange(e)}
              onFocus={e => this.userRef.current.select()}
            />
          ) : (
            <Author>{this.props.user}</Author>
          )}

          <Button>{this.state.isEditing ? "Save" : "Edit"}</Button>
        </HorizontalLayout>
      </form>
    )
  }
}
