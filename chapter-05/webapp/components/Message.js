import React, { Component } from "react"
import dayjs from "dayjs"
import relativeTime from "dayjs/plugin/relativeTime"
dayjs.extend(relativeTime)
import {
  TimeAgo,
  Author,
  MessageText,
  HorizontalLayout,
  MessageWrapper,
  Image,
  ImageWrapper
} from "./Styled"

export default ({ message }) => (
  <MessageWrapper>
    <HorizontalLayout>
      <HorizontalLayout justifyContent="flex-start">
        <Author>{message.author}</Author>{" "}
        <MessageText>{message.message}</MessageText>
      </HorizontalLayout>
      <TimeAgo>{dayjs(message.date).fromNow()}</TimeAgo>
    </HorizontalLayout>
    <ImageWrapper>
      {message.image ? <Image src={message.image} /> : null}
    </ImageWrapper>
  </MessageWrapper>
)
