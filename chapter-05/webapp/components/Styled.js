import styled, { injectGlobal } from "styled-components"
const mainColor = "#0a5"
const secondaryColor = "white"
const pageWidth = "40em"

injectGlobal`
  html {
    background-color: white;
    color: black;
    font-size: 1.3vw;
    font-family: 'Lato', sans-serif;
  }
`

export const Space = styled.div`
  width: ${({ marginLeft }) => marginLeft};
  /* margin-left: ${({ marginLeft }) => marginLeft}; */
`

export const Page = styled.div`
  display: flex;
  justify-content: center;
  flex-direction: column;
`

export const HorizontalLayout = styled.div`
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  justify-content: ${({ justifyContent = "space-between" }) => justifyContent};
`

export const Header = styled.div`
  font-size: 5em;
  font-weight: bolder;
  color: ${mainColor};
`

export const RoomName = styled.h2`
  color: ${mainColor};
`

export const MessageList = styled.ul`
  width: ${pageWidth}
  list-style-type: none;
  padding-left: 0;
`

export const MessageWrapper = styled.li`
  margin-bottom: 0.5em;
  &:hover {
    color: ${mainColor};
  }
`

export const Image = styled.img`
  height: 10em;
`

export const ImageWrapper = styled.div`
  margin-top: 0.5em;
`

export const Author = styled.label`
  color: ${mainColor};
  /* font-size: 0.8em; */
  height: 1.1em;
  width: 6em;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  line-height: 100%;
  /* background: brown; */
  /* text-transform: uppercase; */
`

export const MessageText = styled.label`
  font-weight: bolder;
  height: 1.1em;
  line-height: 100%;
`

export const TimeAgo = styled.label`
  font-size: 0.7em;
  color: gray;
  line-height: 100%;
`

export const NicknameInput = styled.input`
  margin: 0;
  width: 100;
  height: 26;
`

export const MessageInput = styled.input`
  margin-top: 0;
  width: 46.8em;
  height: 32px;
  line-height: 32px;
  padding-left: 8px;
  border: 1px solid ${mainColor};
  font-family: "Lato", sans-serif;
  font-size: 0.8em;
  font-weight: bold;
  vertical-align: bottom;
`

export const Button = styled.button`
  margin: 0;
  width: 50px;
  height: 32px;
  line-height: 32px;
  color: ${secondaryColor};
  background-color: ${mainColor};
  font-family: "Lato", sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.2em;
  border: 0;
  padding: 0;
`
